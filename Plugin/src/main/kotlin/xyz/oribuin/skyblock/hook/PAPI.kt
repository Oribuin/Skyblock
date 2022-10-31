package xyz.oribuin.skyblock.hook

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.utils.HexUtils
import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.manager.ConfigurationManager.Setting
import xyz.oribuin.skyblock.util.asMember
import xyz.oribuin.skyblock.util.formatEnum
import xyz.oribuin.skyblock.util.getIsland

class PAPI(private val rosePlugin: RosePlugin) : PlaceholderExpansion() {

    override fun getIdentifier(): String = "Skyblock"

    override fun getAuthor(): String = "Oribuin"

    override fun getVersion(): String = this.rosePlugin.description.version

    override fun persist(): Boolean = true

    override fun canRegister(): Boolean = true

    @Suppress("DEPRECATION")
    override fun onPlaceholderRequest(player: Player, params: String): String? {

        val island = player.getIsland(this.rosePlugin)
        val member = player.asMember(this.rosePlugin)
        val nullPlaceholder = Setting.NULL_PLACEHOLDER.string



        return when (params) {
            // Basic island info
            "island_name" -> HexUtils.colorify(island?.settings?.name ?: nullPlaceholder)
            "island_biome" -> island?.settings?.biome?.name?.formatEnum() ?: nullPlaceholder
            "island_size" -> island?.members?.size?.toString() ?: nullPlaceholder
            "island_owner" -> island?.ownerMember?.offlinePlayer?.name ?: nullPlaceholder

            // Island Member Info
            "island_border" -> member.border.name.formatEnum()
            "has_island" -> member.hasIsland.toString()
            "island_role" -> member.role.name.formatEnum()

            // Warp Information
            "warp_name" -> island?.warp?.name ?: nullPlaceholder
            "warp_categories" -> island?.warp?.category?.formatted()
            "warp_visits" -> island?.warp?.visits?.toString() ?: nullPlaceholder
            "warp_upvotes" -> island?.warp?.votes?.toString() ?: nullPlaceholder
            else -> nullPlaceholder

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