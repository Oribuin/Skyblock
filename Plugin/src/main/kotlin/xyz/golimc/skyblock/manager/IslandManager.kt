package xyz.golimc.skyblock.manager

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.event.player.PlayerTeleportEvent
import xyz.golimc.skyblock.SkyblockPlugin
import xyz.golimc.skyblock.island.Island
import xyz.golimc.skyblock.island.Member
import xyz.golimc.skyblock.nms.NMSAdapter
import xyz.golimc.skyblock.util.getManager
import xyz.golimc.skyblock.util.usingPaper
import xyz.golimc.skyblock.world.IslandSchematic
import xyz.oribuin.orilibrary.manager.Manager


class IslandManager(private val plugin: SkyblockPlugin) : Manager(plugin) {

    private val data = this.plugin.getManager<DataManager>()

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
            this.teleportToIsland(member, island)
        }
        return island
    }

    /**
     * Teleport the member to an island
     *
     * @param member The member being teleported.
     * @param island The island
     */
    fun teleportToIsland(member: Member, island: Island) {
        val player = member.offlinePlayer.player ?: return

        if (usingPaper) {
            player.teleportAsync(island.home, PlayerTeleportEvent.TeleportCause.PLUGIN)
        } else {
            player.teleport(island.home, PlayerTeleportEvent.TeleportCause.PLUGIN)
        }

        this.createBorder(member, island)
    }

    /**
     * Create the island border for the player
     *
     * @param member The member who is viewing the border
     * @param island The island with the border surrounding it.
     */
    fun createBorder(member: Member, island: Island) {
        val player = member.offlinePlayer.player ?: return

        NMSAdapter.handler.sendWorldBorder(player, member.border, 200.0, island.center)
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
    fun getIslandChunks(island: Island, world: World): List<Chunk> {
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
     * Get the first position of the island
     *
     * @param world The world the island is in.
     */
    fun getPos1(island: Island, world: World?): Location {
        val size = plugin.getManager<UpgradeManager>().getIslandSize(island)
        val centerInWorld = Location(world, island.center.x, island.center.y, island.center.z)
        return centerInWorld.clone().subtract(Location(world, size / 2.0, 0.0, size / 2.0))
    }

    /**
     * Get the second position of the island
     *
     * @param world The world the island is in.
     */
    fun getPos2(island: Island, world: World?): Location {
        val size = plugin.getManager<UpgradeManager>().getIslandSize(island)
        val centerInWorld = Location(world, island.center.x, island.center.y, island.center.z)
        return centerInWorld.clone().add(Location(world, size / 2.0, 0.0, size / 2.0))
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