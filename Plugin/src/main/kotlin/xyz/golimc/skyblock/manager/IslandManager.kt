package xyz.golimc.skyblock.manager

import org.bukkit.entity.Player
import xyz.golimc.skyblock.SkyblockPlugin
import xyz.golimc.skyblock.island.Island
import xyz.golimc.skyblock.island.Member
import xyz.golimc.skyblock.util.getManager
import xyz.oribuin.orilibrary.manager.Manager
import java.util.*


class IslandManager(private val plugin: SkyblockPlugin) : Manager(plugin) {

    private val data = this.plugin.getManager<DataManager>()

    fun makeIsland(player: Player) {
        TODO("Finish the make island function.")
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