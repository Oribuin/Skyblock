package xyz.golimc.skyblock.manager

import org.bukkit.event.player.PlayerTeleportEvent
import xyz.golimc.skyblock.SkyblockPlugin
import xyz.golimc.skyblock.island.Island
import xyz.golimc.skyblock.island.Member
import xyz.golimc.skyblock.nms.NMSAdapter
import xyz.golimc.skyblock.util.getManager
import xyz.golimc.skyblock.util.usingPaper
import xyz.golimc.skyblock.world.IslandSchematic
import xyz.oribuin.orilibrary.manager.Manager
import java.util.*


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

        val island = data.createIsland(member.uuid, )
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
     * Get a member from the island.
     *
     * @param player The player's UUID
     * @return The member
     */
    fun getMember(player: UUID): Member {
        return data.islandCache.values.stream()
            .map<List<Member>>(Island::members)
            .flatMap { it.stream() }
            .filter { (uuid): Member -> uuid == player }
            .findFirst()
            .orElse(Member(player))
    }
}