package xyz.oribuin.skyblock.gui

import dev.rosewood.rosegarden.RosePlugin
import org.bukkit.Material
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.manager.MenuManager
import xyz.oribuin.skyblock.util.getManager

class PanelGUI(rosePlugin: RosePlugin) : PluginGUI(rosePlugin) {

    private val islandManager = this.rosePlugin.getManager<IslandManager>()
    private val menuManager = this.rosePlugin.getManager<MenuManager>()

    fun openMenu(member: Member) {
        val player = member.onlinePlayer ?: return
        val gui = this.createGUI(player)

        this.put(gui, "border-item", player)
        this.put(gui, "home-item", player) { this.islandManager.teleportHome(member) }
        this.put(gui, "members-item", player) { menuManager[MembersGUI::class].openMenu(member) }
        this.put(gui, "settings-item", player) { menuManager[SettingsGUI::class].openMenu(member) }
        this.put(gui, "warps-item", player) { menuManager[WarpsGUI::class].openMenu(member) }

        gui.open(player)
    }

    override val defaultValues: Map<String, Any>
        get() = mapOf(
            "#0" to "GUI Settings",
            "gui-settings.title" to "Island Panel",
            "gui-settings.rows" to 3,

            "#1" to "Home Item",
            "home-item.material" to Material.CYAN_BED.toString(),
            "home-item.name" to "#a6b2fc&lTeleport Home",
            "home-item.lore" to listOf(
                " &f| &7Click to teleport to",
                " &f| &7your island home!"
            ),
            "home-item.slot" to 10,

            "#2" to "Members Item",
            "members-item.material" to Material.PLAYER_HEAD.toString(),
            "members-item.name" to "#a6b2fc&lMembers",
            "members-item.lore" to listOf(
                " &f| &7Click to view all the members",
                " &f| &7that are on your island!"
            ),
            "members-item.slot" to 14,
            "members-item.owner" to "self",

            "#4" to "Settings Item",
            "settings-item.material" to Material.REDSTONE.toString(),
            "settings-item.name" to "#a6b2fc&lSettings",
            "settings-item.lore" to listOf(
                " &f| &7Click to view & manage your",
                " &f| &7your island settings!"
            ),
            "settings-item.slot" to 15,

            "#5" to "Warps Item",
            "warps-item.material" to Material.SPRUCE_SIGN.toString(),
            "warps-item.name" to "#a6b2fc&lWarp",
            "warps-item.lore" to listOf(
                " &f| &7Click to view & manage your",
                " &f| &7your island warp!"
            ),
            "warps-item.slot" to 16,

            "#6" to "Border Item",
            "border-item.enabled" to true,
            "border-item.material" to Material.BLACK_STAINED_GLASS_PANE.toString(),
            "border-item.name" to " ",
            "border-item.slots" to listOf("0-26")
        )

    override val menuName: String
        get() = "panel-gui"
}