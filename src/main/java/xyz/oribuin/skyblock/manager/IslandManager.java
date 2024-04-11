package xyz.oribuin.skyblock.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.block.Biome;
import xyz.oribuin.skyblock.island.IslandBiome;

import java.util.HashMap;
import java.util.Map;

public class IslandManager extends Manager {

    private final DataManager dataManager = this.rosePlugin.getManager(DataManager.class);
    private final Map<Biome, IslandBiome> biomeMap = new HashMap<>();

    public IslandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        // TODO: Load all biomes from the config

    }

    @Override
    public void disable() {

    }
}

//import dev.rosewood.rosegarden.RosePlugin
//import dev.rosewood.rosegarden.manager.Manager
//import dev.rosewood.rosegarden.utils.HexUtils
//import dev.rosewood.rosegarden.utils.StringPlaceholders
//import net.kyori.adventure.text.Component
//import net.kyori.adventure.text.event.ClickEvent
//import net.kyori.adventure.text.event.HoverEvent
//import org.bukkit.ChatColor
//import org.bukkit.Chunk
//import org.bukkit.Location
//import org.bukkit.Material
//import org.bukkit.OfflinePlayer
//import org.bukkit.World
//import org.bukkit.block.Biome
//import org.bukkit.block.Block
//import org.bukkit.entity.Player
//import org.bukkit.event.player.PlayerTeleportEvent
//import xyz.oribuin.skyblock.gui.CreateGUI
//import xyz.oribuin.skyblock.island.BiomeOption
//import xyz.oribuin.skyblock.island.Island
//import xyz.oribuin.skyblock.island.member.Member
//import xyz.oribuin.skyblock.island.warp.Warp
//import xyz.oribuin.skyblock.manager.ConfigurationManager.Setting
//import xyz.oribuin.skyblock.nms.NMSAdapter
//import xyz.oribuin.skyblock.util.cache
//import xyz.oribuin.skyblock.util.color
//import xyz.oribuin.skyblock.util.getManager
//import xyz.oribuin.skyblock.util.parseEnum
//import xyz.oribuin.skyblock.util.send
//import xyz.oribuin.skyblock.world.IslandSchematic
//import java.util.*
//
//class IslandManager(rosePlugin: RosePlugin) : Manager(rosePlugin) {
//
//    private val dataManager = this.rosePlugin.getManager<skyblock.manager.DataManager>()
//    private val inviteMap = mutableMapOf<UUID, UUID>()
//    val biomeMap = mutableMapOf<Biome, xyz.oribuin.skyblock.island.BiomeOption>()
//
//    @Suppress("deprecation")
//    override fun reload() {
//        this.dataManager.loadIslands()
//        val section = this.rosePlugin.config.getConfigurationSection("biomes") ?: return
//
//        // Add all the biomes into the cache.
//        section.getKeys(false).forEach {
//            val biome = skyblock.util.parseEnum(Biome::class, it.uppercase())
//            val option = xyz.oribuin.skyblock.island.BiomeOption(biome)
//            option.cost = section.getDouble("$it.cost")
//            option.icon = skyblock.util.parseEnum(Material::class, section.getString("$it.icon") ?: "GRASS_BLOCK")
//
//            this.biomeMap[biome] = option
//        }
//    }
//
//
//    /**
//     * Get the island member for the given player.
//     *
//     * @param player The player to get the island member for.
//     * @return The island member for the given player.
//     */
//    fun getMember(player: Player): xyz.oribuin.skyblock.island.member.Member = this.dataManager.getMember(player.uniqueId)
//
//    /**
//     * Get the island for the given uuid.
//     *
//     * @param uuid The uuid player to get the island for.
//     * @return The island for the given player.
//     *
//     */
//    fun getMember(uuid: UUID): xyz.oribuin.skyblock.island.member.Member = this.dataManager.getMember(uuid)
//
//    /**
//     * Get the island for the given offline player.
//     *
//     * @param player The offline player to get the island for.
//     * @return The island for the given player.
//     */
//    fun getMember(player: OfflinePlayer): xyz.oribuin.skyblock.island.member.Member = this.dataManager.getMember(player.uniqueId)
//
//    /**
//     * Get the island for the given player.
//     *
//     * @param player The player to get the island for.
//     * @return The island for the given player.
//     */
//    fun getIsland(player: Player): xyz.oribuin.skyblock.island.Island? = this.getMember(player).let { this.dataManager.getIsland(it.island) }
//
//    /**
//     * Get the island for the given uuid.
//     *
//     * @param uuid The uuid player to get the island for.
//     * @return The island for the given player.
//     */
//    fun getIsland(uuid: UUID): xyz.oribuin.skyblock.island.Island? = this.getMember(uuid).let { this.dataManager.getIsland(it.island) }
//
//    /**
//     * Get the island for the given offline player.
//     *
//     * @param player The offline player to get the island for.
//     * @return The island for the given player.
//     */
//    fun getIsland(player: OfflinePlayer): xyz.oribuin.skyblock.island.Island? = this.getIsland(player.uniqueId)
//
//
//    /**
//     * Get the island for the given member
//     *
//     * @param member The member to get the island for.
//     * @return The island for the given member.
//     */
//    fun getIsland(member: xyz.oribuin.skyblock.island.member.Member): xyz.oribuin.skyblock.island.Island? = this.dataManager.getIsland(member.island)
//
//    override fun disable() {
//        this.dataManager.saveIslands()
//    }
//
//    /**
//     * Create an island for the member.
//     *
//     * @param member The owner of the island.
//     * @param schematic The island design.
//     */
//    fun makeIsland(member: xyz.oribuin.skyblock.island.member.Member, schematic: IslandSchematic): xyz.oribuin.skyblock.island.Island {
//        val memberIsland = dataManager.getIsland(member.island)
//
//        if (memberIsland != null)
//            return memberIsland
//
//        member.onlinePlayer?.let { this.rosePlugin.send(it, "command-create-success") }
//
//        val island = dataManager.createIsland(member.uuid)
//        schematic.paste(this.rosePlugin, island.center) { this.teleport(member, island.home) }
//        return island
//    }
//
//    fun deleteIsland(island: xyz.oribuin.skyblock.island.Island) {
//        this.dataManager.deleteIsland(island)
//    }
//
//    /**
//     * Teleport the member to an island
//     *
//     * @param member The member being teleported.
//     * @param location The location the player is being teleported to
//     */
//    fun teleport(member: xyz.oribuin.skyblock.island.member.Member, location: Location) {
//        val island = this.getIslandFromLoc(location) ?: return
//        val player = member.onlinePlayer ?: return
//
//        if (!island.settings.public || island.settings.banned.getUUIDs().contains(member.uuid)) {
//            return
//        }
//
//        player.fallDistance = 0f
//        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN).thenRun {
//            this.createBorder(member, island)
//        }
//
//    }
//
//    /**
//     * Teleport the member to their island
//     *
//     * @param member The member being teleported.
//     */
//    fun teleportHome(member: xyz.oribuin.skyblock.island.member.Member) = this.getIsland(member)?.let { this.teleport(member, it.home) }
//
//    /**
//     * Create the island border for the player
//     *
//     * @param member The member who is viewing the border
//     * @param island The island with the border surrounding it.
//     */
//    fun createBorder(member: xyz.oribuin.skyblock.island.member.Member, island: xyz.oribuin.skyblock.island.Island) {
//        val player = member.onlinePlayer ?: return
//        NMSAdapter.handler.sendWorldBorder(player, member.border, Setting.ISLAND_SIZE.double, island.center)
//    }
//
//    /**
//     * Get an island from the ID
//     *
//     * @param id The id of the island.
//     * @return The island with the matching ID.
//     */
//    private fun getIslandFromId(id: Int): xyz.oribuin.skyblock.island.Island? {
//        return this.dataManager.getIsland(id)
//    }
//
//    /**
//     * Get the island from the location.
//     *
//     * @param location The location of the island
//     * @return The island with the location in range.
//     */
//    fun getIslandFromLoc(location: Location): xyz.oribuin.skyblock.island.Island? {
//        return this.dataManager.islandCache.values.find { this.isInside(it, location.x, location.z) }
//    }
//
//    /**
//     * Check if the x and z coords are inside the island
//     *
//     * @param x The X Axis
//     * @param z The Z Axis
//     * @return true if it's inside the island.
//     */
//    private fun isInside(island: xyz.oribuin.skyblock.island.Island, x: Double, z: Double, world: World? = null): Boolean {
//        val pos1 = getPos1(island, world)
//        val pos2 = getPos2(island, world)
//
//        return pos1.x <= x && pos1.z <= z && pos2.x >= x && pos2.z >= z
//    }
//
//    /**
//     * Get all players that are inside the island
//     *
//     * @param island The island to check
//     * @return A list of players inside the island
//     */
//    fun getPlayersOnIsland(island: xyz.oribuin.skyblock.island.Island): List<Player> {
//        val worldManager = this.rosePlugin.getManager<skyblock.manager.WorldManager>()
//
//        var players = emptyList<Player>()
//        for (world in worldManager.worlds.values) {
//            players = world.players.filter { this.isInside(island, it.location.x, it.location.z, world) }
//            if (players.isNotEmpty())
//                return players
//        }
//
//        return players
//    }
//
//    /**
//     * Get the all the chunks in the island
//     *
//     * @param island The island.
//     * @param world The world the chunks are in
//     * @return the island chunks.
//     */
//    fun getIslandChunks(island: xyz.oribuin.skyblock.island.Island, world: World): List<Chunk> {
//        val chunks = mutableListOf<Chunk>()
//
//        val pos1 = this.getPos1(island, world)
//        val pos2 = this.getPos2(island, world)
//
//        val minX = pos1.blockX shr 4
//        val minZ = pos1.blockZ shr 4
//
//        val maxX = pos2.blockX shr 4
//        val maxZ = pos2.blockZ shr 4
//
//        for (x in minX..maxX)
//            for (z in minZ..maxZ)
//                chunks.add(world.getChunkAt(x, z))
//
//        return chunks
//    }
//
//    /**
//     * Sets the island's chunks to the current biome.
//     *
//     * @param island The island.
//     */
//    fun setIslandBiome(island: xyz.oribuin.skyblock.island.Island) {
//        val chunks = this.getIslandChunks(island, world = this.rosePlugin.getManager<skyblock.manager.WorldManager>().overworld)
//        chunks.forEach { chunk -> chunk.blocks.forEach { it.biome = island.settings.biome } }
//
//        this.rosePlugin.server.scheduler.runTaskLater(this.rosePlugin, Runnable {
//            NMSAdapter.handler.sendChunks(chunks, this.getPlayersOnIsland(island))
//        }, 2)
//
//    }
//
//    /**
//     * Get the first position of the island
//     *
//     * @param world The world the island is in.
//     */
//    private fun getPos1(island: xyz.oribuin.skyblock.island.Island, world: World?): Location {
//        val size = Setting.ISLAND_SIZE.double
//        val centerInWorld = Location(world, island.center.x, island.center.y, island.center.z)
//        return centerInWorld.clone().subtract(Location(world, (size / 2.0) + 1, 0.0, (size / 2.0)))
//    }
//
//    /**
//     * Get the second position of the island
//     *
//     * @param world The world the island is in.
//     */
//    private fun getPos2(island: xyz.oribuin.skyblock.island.Island, world: World?): Location {
//        val size = Setting.ISLAND_SIZE.double
//        val centerInWorld = Location(world, island.center.x, island.center.y, island.center.z)
//        return centerInWorld.clone().add(Location(world, (size / 2.0) + 1, 0.0, (size / 2.0) + 1))
//    }
//
//    /**
//     * Get all the blocks in a chunk
//     *
//     * @return the chunk blocks.
//     */
//    private val Chunk.blocks: List<Block>
//        get() {
//            val blocks = mutableListOf<Block>()
//
//            val baseLocation = Location(this.world, this.x * 16.0, 0.0, this.z * 16.0)
//
//            for (x in 0..15)
//                for (z in 0..15)
//                    for (y in this.world.minHeight until this.world.maxHeight)
//                        blocks.add(baseLocation.clone().add(x.toDouble(), y.toDouble(), z.toDouble()).block)
//
//            return blocks
//        }
//
//    /**
//     * Teleport a player to the island warp
//     *
//     * @param warp The island warp
//     * @param member The member being teleported.
//     */
//    fun warpTeleport(warp: skyblock.island.Warp, member: xyz.oribuin.skyblock.island.member.Member) {
//
//        val island = this.getIslandFromId(warp.key) ?: return
//
//        // Check if the island is locked or the warp is disabled
//        if (!island.settings.public || island.warp.disabled)
//            return
//
//        // Check if the user is banned from the island
//        if (member.onlinePlayer?.hasPermission("skyblock.island.bypass") != true && island.settings.banned.getUUIDs()
//                .contains(member.uuid)
//        )
//            return
//
//        // Check if the user has already visited and if the user teleported is part of the island
//
//        if (!warp.visitUsers.contains(member.uuid) && member.island != warp.key) {
//            warp.visits++
//            warp.visitUsers.add(member.uuid)
//
//            island.warp = warp
//            island.cache(this.rosePlugin)
//        }
//
//        this.teleport(member, warp.location)
//    }
//
//    /**
//     * Upvote an island warp from a player
//     *
//     * @param warp The island warp
//     * @param member The member upvoting the warp
//     */
//    fun upvoteWarp(warp: skyblock.island.Warp, member: xyz.oribuin.skyblock.island.member.Member) {
//
//        // Check if the user is trying to upvote their own island
//        if (member.island == warp.key) {
//            member.onlinePlayer?.let { this.rosePlugin.send(it, "command-warp-upvote-self-upvote") }
//            return
//        }
//
//        // Check if the user has already upvoted the island
//        if (warp.votedUsers.contains(member.uuid)) {
//            member.onlinePlayer?.let { this.rosePlugin.send(it, "command-warp-upvote-already-voted") }
//            return
//        }
//
//        // Check if the user is banned from the island
//        if (member.onlinePlayer?.hasPermission("skyblock.island.bypass") != true && this.getIslandFromId(warp.key)?.settings?.banned?.getUUIDs()
//                ?.contains(member.uuid) == true
//        ) {
//            member.onlinePlayer?.let { this.rosePlugin.send(it, "command-warp-banned") }
//            return
//        }
//
//        // Add the user to the list of users who have upvoted the island
//        warp.votedUsers.add(member.uuid)
//        warp.votes++
//
//        val island = this.getIslandFromId(warp.key) ?: return
//        island.warp = warp
//        island.cache(this.rosePlugin)
//
//        member.onlinePlayer?.let { this.rosePlugin.send(it, "command-warp-upvote-success") }
//    }
//
////    /**
////     * Get a warp by the name of it
////     *
////     * @param name The name of the warp.
////     * @return The warp with the matching name.
////     */
////    fun getWarpByName(name: String): Warp? = this.dataManager.islandCache.values
////        .map { it.warp }
////        .find { HexUtils.colorify(it.name).lowercase().let { x -> ChatColor.stripColor(x) }.equals(name.lowercase(), true) }
//
//
//    fun getWarpsByName(name: String): skyblock.island.Warp? {
//        for (warp in this.dataManager.islandCache.values.map { it.warp }) {
//            val warpName = HexUtils.colorify(warp.name.lowercase()).lowercase().let { x -> ChatColor.stripColor(x) }
//
//            if (warpName.equals(name.lowercase(), true)) {
//                return warp
//            }
//        }
//
//        return null
//    }
//
//    /**
//     * Get a warp by the category of it
//     *
//     * @param category The warp category type
//     * @return The warp with the matching category.
//     */
//    fun getWarpByCategory(category: skyblock.island.Warp.Category.Type): List<skyblock.island.Warp> = this.dataManager.islandCache.values
//        .map { it.warp }
//        .filter { it.category.types.contains(category.name) }
//        .toMutableList()
//
//    /**
//     * Get all names for warps in a category
//     *
//     * @return The warp names.
//     */
//    fun getWarpNames(): MutableList<String> = this.dataManager.islandCache.values
//        .map { it.warp }
//        .mapNotNull { ChatColor.stripColor(it.name) }
//        .toMutableList()
//
//    /**
//     * Send all members of an island a message from the locale
//     *
//     * @param island The island.
//     * @param messageId The message id.
//     * @param placeholders The message placeholders.
//     */
//    fun sendMembersMessage(
//        island: xyz.oribuin.skyblock.island.Island,
//        messageId: String,
//        placeholders: StringPlaceholders = StringPlaceholders.empty()
//    ) {
//        island.members.mapNotNull { it.onlinePlayer }.forEach { this.rosePlugin.send(it, messageId, placeholders) }
//    }
//
//    /**
//     * Get all the banned players from an island
//     *
//     * @param island The island.
//     * @return The banned players.
//     */
//    fun getBannedUsers(island: xyz.oribuin.skyblock.island.Island): List<UUID> =
//        this.dataManager.islandCache[island.key]?.settings?.banned?.getUUIDs() ?: emptyList()
//
//
//    /**
//     * Send a player an island invite
//     *
//     * @param from The player sending the invite.
//     * @param to The player receiving the invite.
//     *
//     */
//    fun sendInvite(from: Player, to: Player) {
//
//        // Check if the players are the same person
//        if (from.uniqueId == to.uniqueId) {
//            this.rosePlugin.send(from, "island-invite-cant-invite-self")
//            return
//        }
//
//        // Get members
//        val fromMember = this.getMember(from)
//        val toMember = this.getMember(to)
//        // Get island
//        val fromIsland = this.getIsland(fromMember)
//
//        // Check if the fromPlayer has an island
//        if (fromIsland == null) {
//            xyz.oribuin.skyblock.gui.CreateGUI(this.rosePlugin).openMenu(fromMember)
//            return
//        }
//
//        // Check if the toMember has an island
//        if (toMember.hasIsland) {
//            this.rosePlugin.send(from, "island-invite-has-island")
//            return
//        }
//
//        // Check if the island can fit the player
//        if (fromIsland.members.size >= Setting.MAX_MEMBERS.int) {
//            this.rosePlugin.send(from, "island-invite-full")
//            return
//        }
//
//        val localeManager = this.rosePlugin.getManager<skyblock.manager.LocaleManager>()
//        localeManager.sendMessage(from, "island-invite-sent", StringPlaceholders.of("player", to.name))
//        val prefix = localeManager.getLocaleMessage("prefix")
//        val receivedMessage = prefix + localeManager.getLocaleMessage(
//            "island-invite-received",
//            StringPlaceholders.of("player", from.name)
//        )
//
//        to.sendMessage(
//            Component.text(receivedMessage.color())
//                .clickEvent(ClickEvent.suggestCommand("/is invite accept ${from.name}"))
//                .hoverEvent(HoverEvent.showText(Component.text("#a6b2fcClick to accept this request.".color())))
//        )
//
//        this.inviteMap[from.uniqueId] = to.uniqueId
//    }
//
//    /**
//     * Accept an island invite from a player
//     *
//     * @param member The player accepting the invite.
//     */
//    fun acceptInvite(member: xyz.oribuin.skyblock.island.member.Member) {
//
//        val player = member.onlinePlayer ?: return // Member cannot be offline
//
//        val invite = this.inviteMap.values.find { it == member.uuid }
//        if (invite == null) {
//            this.rosePlugin.send(player, "command-invite-no-invite")
//            return
//        }
//
//
//        val from = this.getMember(invite) // The player who sent the invite
//        val island = this.getIsland(from) // The island the player is being invited to
//
//        // Check if the person who sent the invite has an island
//        if (island == null) {
//            this.rosePlugin.send(player, "command-invite-no-island")
//            return
//        }
//
//        // Check if the member already has an island
//        if (member.hasIsland) {
//            this.rosePlugin.send(player, "command-invite-has-island")
//            return
//        }
//
//
//        // check if the member can fit on the island
//        if (island.members.size >= Setting.MAX_MEMBERS.int) {
//            this.rosePlugin.send(player, "command-invite-island-full")
//            return
//        }
//
//
//        // remove anything from inviteMap if member is the value
//        this.inviteMap.filterValues { it == member.uuid }.forEach { this.inviteMap.remove(it.key) }
//
//
//        island.members.add(member)
//        island.cache(this.rosePlugin)
//        this.rosePlugin.send(
//            player,
//            "command-invite-accept-success",
//            StringPlaceholders.of("island", island.settings.name)
//        )
//        this.sendMembersMessage(
//            island,
//            "command-invite-accept-joined",
//            StringPlaceholders.of("player", player.name)
//        )
//    }
//
//    /**
//     * Deny an island invite from a player
//     *
//     * @param player The player denying the invite.
//     */
//    fun denyInvite(player: Player) {
//        val invite = this.inviteMap.entries.find { it.value == player.uniqueId }
//        if (invite == null) {
//            this.rosePlugin.send(player, "command-invite-no-invite")
//            return
//        }
//
//        this.inviteMap.filterValues { it == player.uniqueId }.forEach { this.inviteMap.remove(it.key) }
//
//
//        this.rosePlugin.send(player, "command-invite-deny-denied")
//
//        this.getMember(invite.key).onlinePlayer?.let {
//            this.rosePlugin.send(it, "command-invite-deny-other", StringPlaceholders.of("player", player.name))
//        }
//    }
//
//    fun getIslands(): List<xyz.oribuin.skyblock.island.Island> = this.dataManager.islandCache.values.toList()
//
//}