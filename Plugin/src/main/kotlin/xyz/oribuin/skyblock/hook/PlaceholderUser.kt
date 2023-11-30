package xyz.oribuin.skyblock.hook

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.utils.HexUtils
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.manager.ConfigurationManager.Setting.NULL_PLACEHOLDER
import xyz.oribuin.skyblock.util.asMember
import xyz.oribuin.skyblock.util.getIsland

// a god awful placeholder class
class PlaceholderUser {

    var updateTime: Long = System.currentTimeMillis() // The time the user was last updated.

    var islandName: String = NULL_PLACEHOLDER.string
    var islandBiome: String = NULL_PLACEHOLDER.string
    var islandSize: String = NULL_PLACEHOLDER.string
    var islandOwner: String = NULL_PLACEHOLDER.string

    var border = "BLUE"
    var hasIsland = "false"
    var role = "Member"

    var warpName: String = NULL_PLACEHOLDER.string
    var warpCategories: String = NULL_PLACEHOLDER.string
    var warpVisits: String = "0"
    var warpUpvotes: String = "0"

    companion object {

        /**
         * Update the user's placeholders for the island
         *
         * @param plugin The plugin instance
         *  @param player The player
         */
        fun update(plugin: RosePlugin, player: Player): PlaceholderUser {
            val defaultUser = PlaceholderUser() // Recreate the default user to reset the values
            val island = player.getIsland(plugin)

            if (island != null) {
                defaultUser.islandName = HexUtils.colorify(island.settings.name)
                defaultUser.islandBiome = island.settings.biome.name
                defaultUser.islandSize = island.members.size.toString()
                defaultUser.islandOwner = island.ownerMember.offlinePlayer.name ?: NULL_PLACEHOLDER.string

                defaultUser.warpName = island.warp.name
                defaultUser.warpCategories = island.warp.category.formatted()
                defaultUser.warpVisits = island.warp.visits.toString()
                defaultUser.warpUpvotes = island.warp.votes.toString()
            }

            val member = player.asMember(plugin)
            defaultUser.border = member.border.name
            defaultUser.hasIsland = member.hasIsland.toString()
            defaultUser.role = member.role.name

            return defaultUser
        }

    }

}