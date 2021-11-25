package xyz.oribuin.skyblock.manager

import org.bukkit.*
import org.bukkit.block.Biome
import org.bukkit.block.Block
import org.bukkit.event.player.PlayerTeleportEvent
import xyz.oribuin.orilibrary.manager.Manager
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.BiomeOption
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.nms.NMSAdapter
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.parseEnum
import xyz.oribuin.skyblock.util.usingPaper
import xyz.oribuin.skyblock.world.IslandSchematic


class IslandManager(private val plugin: SkyblockPlugin) : Manager(plugin) {

    private val data = this.plugin.getManager<DataManager>()
    val biomeMap = mutableMapOf<Biome, BiomeOption>()

    override fun enable() {
        val section = this.plugin.config.getConfigurationSection("biomes") ?: return

        // Add all the biomes into the cache.
        section.getKeys(false).forEach {
            val biome = parseEnum(Biome::class, it.uppercase())
            val option = BiomeOption(biome)
            option.cost = section.getDouble("$it.cost")
            option.icon = parseEnum(Material::class, section.getString("$it.icon") ?: "GRASS_BLOCK")

            this.biomeMap[biome] = option
        }
    }

    /**
     * Create an island for the member.
     *
     * @param member The owner of the island.
     * @param schematic The island design.
     */
    fun makeIsland(member: Member, schematic: IslandSchematic): Island {
        val memberIsland = data.getIsland(member.island)

        if (memberIsland != null)
            return memberIsland

        val island = data.createIsland(member.uuid)
        schematic.paste(plugin, island.center) {
            this.teleport(member, island.home)
        }
        return island
    }

    /**
     * Teleport the member to an island
     *
     * @param player The member being teleported.
     * @param island The island
     */
    fun teleport(member: Member, location: Location) {
        val player = member.offlinePlayer.player ?: return
        player.fallDistance = 0f

        when (usingPaper) {
            true -> player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN)
            false -> player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)
        }

        val island = this.getIslandFromLoc(location) ?: return
        this.plugin.server.scheduler.runTaskLater(this.plugin, Runnable { this.createBorder(member, island) }, 1)
    }

    /**
     * Create the island border for the player
     *
     * @param member The member who is viewing the border
     * @param island The island with the border surrounding it.
     */
    fun createBorder(member: Member, island: Island) {
        val player = member.offlinePlayer.player ?: return
        val size = this.plugin.getManager<UpgradeManager>().getIslandSize(island)
        NMSAdapter.handler.sendWorldBorder(player, member.border, size.toDouble(), island.center)
    }

    /**
     * Get an island from the ID
     *
     * @param id The id of the island.
     * @return The island with the matching ID.
     */
    fun islandFromID(id: Int): Island? {
        return this.data.islandCache.filter { entry -> entry.key == id }[0]
    }

    /**
     * Get the island from the location.
     *
     * @param location The location of the island
     * @return The island with the location in range.
     */
    fun getIslandFromLoc(location: Location): Island? {
        return this.data.islandCache.values.find { it.isInside(location.x, location.z) }
    }

    /**
     * Check if the x and z coords are inside the island
     *
     * @param x The X Axis
     * @param z The Z Axis
     * @return true if it's inside the island.
     */
    private fun Island.isInside(x: Double, z: Double): Boolean {
        val pos1 = getPos1(this, null)
        val pos2 = getPos2(this, null)

        return pos1.x <= x && pos1.z <= z && pos2.x >= x && pos2.z >= z
    }

    /**
     * Get the all the chunks in the island
     *
     * @param island The island.
     * @param world The world the chunks are in
     * @return the island chunks.
     */
    private fun getIslandChunks(island: Island, world: World): List<Chunk> {
        val chunks = mutableListOf<Chunk>()

        val pos1 = this.getPos1(island, world)
        val pos2 = this.getPos2(island, world)

        val minX = pos1.blockX shr 4
        val minZ = pos1.blockZ shr 4

        val maxX = pos2.blockX shr 4
        val maxZ = pos2.blockZ shr 4

        for (x in minX..maxX)
            for (z in minZ..maxZ)
                chunks.add(world.getChunkAt(x, z))

        return chunks
    }

    /**
     * Sets the island's chunks to the current biome.
     *
     * @param island The island.
     */
    fun setIslandBiome(island: Island) {
        val chunks = this.getIslandChunks(island, world = this.plugin.getManager<WorldManager>().overworld)
        chunks.forEach { chunk -> chunk.blocks.forEach { it.biome = island.settings.biome } }

        this.plugin.server.scheduler.runTaskLater(this.plugin, Runnable { NMSAdapter.handler.sendChunks(chunks, Bukkit.getOnlinePlayers().toList()) }, 2)
    }

    /**
     * Get the first position of the island
     *
     * @param world The world the island is in.
     */
    private fun getPos1(island: Island, world: World?): Location {
        val size = plugin.getManager<UpgradeManager>().getIslandSize(island)
        val centerInWorld = Location(world, island.center.x, island.center.y, island.center.z)
        return centerInWorld.clone().subtract(Location(world, size / 2.0, 0.0, size / 2.0))
    }

    /**
     * Get the second position of the island
     *
     * @param world The world the island is in.
     */
    private fun getPos2(island: Island, world: World?): Location {
        val size = plugin.getManager<UpgradeManager>().getIslandSize(island)
        val centerInWorld = Location(world, island.center.x, island.center.y, island.center.z)
        return centerInWorld.clone().add(Location(world, size / 2.0, 0.0, size / 2.0))
    }

    /**
     * Get all the blocks in a chunk
     *
     * @return the chunk blocks.
     */
    private val Chunk.blocks: List<Block>
        get() {
            val blocks = mutableListOf<Block>()

            val baseLocation = Location(this.world, this.x * 16.0, 0.0, this.z * 16.0)

            for (x in 0..15)
                for (z in 0..15)
                    for (y in 0 until this.world.maxHeight)
                        blocks.add(baseLocation.clone().add(x.toDouble(), y.toDouble(), z.toDouble()).block)

            return blocks
        }


    //    /**
    //     * Get a member from the island.
    //     *
    //     * @param player The player's UUID
    //     * @return The member
    //     */
    //    fun getMember(player: UUID): Member {
    //        return data.islandCache.values.stream()
    //            .map<List<Member>>(Island::members)
    //            .flatMap { it.stream() }
    //            .filter { (uuid): Member -> uuid == player }
    //            .findFirst()
    //            .orElse(Member(player))
    //    }
}