package xyz.oribuin.skyblock.hook

import dev.rosewood.rosegarden.RosePlugin
import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.manager.ConfigurationManager.Setting
import xyz.oribuin.skyblock.util.formatEnum
import java.util.*
import java.util.concurrent.TimeUnit

class PAPI(private val rosePlugin: RosePlugin) : PlaceholderExpansion() {

    private val updateSpeed = TimeUnit.SECONDS.toMillis(Setting.PLACEHOLDER_UPDATE_SPEED.long)
    private val userData = mutableMapOf<UUID, PlaceholderUser>()

    override fun getIdentifier(): String = "Skyblock"

    override fun getAuthor(): String = "Oribuin"

    override fun getVersion(): String = this.rosePlugin.description.version

    override fun persist(): Boolean = true

    override fun canRegister(): Boolean = true

    @Suppress("DEPRECATION")
    override fun onPlaceholderRequest(player: Player, params: String): String? {

        // force update the user data when requested
        if (params.equals("update", true)) {
            this.userData[player.uniqueId] = PlaceholderUser.update(this.rosePlugin, player)
            return "Updated"
        }

        // get the placeholder user
        var placeholderUser = this.userData[player.uniqueId]

        // check if it has been more than 5 minutes since the last update

        if (placeholderUser == null || (System.currentTimeMillis() - placeholderUser.updateTime) > this.updateSpeed) {
            placeholderUser = PlaceholderUser.update(this.rosePlugin, player)
            this.userData[player.uniqueId] = placeholderUser
        }

        return when (params) {
            // Basic island info
            "island_name" -> placeholderUser.islandName
            "island_biome" -> placeholderUser.islandBiome
            "island_size" -> placeholderUser.islandOwner
            "island_owner" -> placeholderUser.islandOwner

            // Island Member Info
            "island_border" -> placeholderUser.border.formatEnum()
            "has_island" -> placeholderUser.hasIsland
            "island_role" -> placeholderUser.role.formatEnum()

            // Warp Information
            "warp_name" -> placeholderUser.warpName
            "warp_categories" -> placeholderUser.warpCategories
            "warp_visits" -> placeholderUser.warpVisits
            "warp_upvotes" -> placeholderUser.warpUpvotes
            else -> null

        }

    }

    companion object {
        /**
         * Apply placeholders to a string
         *
         * @param player The player to apply the placeholders to
         * @param text The text to apply the placeholders to
         * @return The string with the placeholders applied
         */
        fun apply(player: Player?, text: String) = PlaceholderAPI.setPlaceholders(player, text)
    }

    init {
        if (this.rosePlugin.server.pluginManager.isPluginEnabled("PlaceholderAPI"))
            this.register()
    }
}