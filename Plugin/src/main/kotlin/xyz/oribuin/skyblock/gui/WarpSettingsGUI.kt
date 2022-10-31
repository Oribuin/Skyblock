package xyz.oribuin.skyblock.gui

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.utils.StringPlaceholders
import dev.triumphteam.gui.guis.Gui
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.island.Member.Role
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.manager.MenuManager
import xyz.oribuin.skyblock.util.ItemBuilder
import xyz.oribuin.skyblock.util.cache
import xyz.oribuin.skyblock.util.format
import xyz.oribuin.skyblock.util.formatEnum
import xyz.oribuin.skyblock.util.getIsland
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.getMenu
import xyz.oribuin.skyblock.util.send

class WarpSettingsGUI(rosePlugin: RosePlugin) : PluginGUI(rosePlugin) {

    private val manager = this.rosePlugin.getManager<IslandManager>()

    fun openMenu(member: Member) {
        val island = member.getIsland(this.rosePlugin) ?: return
        val player = member.onlinePlayer ?: return

        val gui = this.createGUI(player)
        this.put(gui, "border-item", player)
        this.put(gui, "back-item", player) { this.rosePlugin.getMenu(WarpsGUI::class).openMenu(member) }
        this.setSettings(gui, member, island)

        gui.open(player)
    }

    private fun setSettings(gui: Gui, member: Member, island: Island) {
        val player = member.onlinePlayer ?: return

        this.put(gui, "warp-name", player, StringPlaceholders.single("name", island.warp.name)) {
            if (member.role == Role.MEMBER) {
                this.rosePlugin.send(player, "island-no-permission")
                gui.close(player)
                return@put
            }

            AnvilGUI.Builder()
                .plugin(this.rosePlugin)
                .text(island.warp.name)
                .title(island.warp.name)
                .itemLeft(ItemBuilder.filler(Material.NAME_TAG))
                .onComplete { _, text ->

                    if (text.equals(island.warp.name, ignoreCase = true))
                        return@onComplete AnvilGUI.Response.close()

                    island.warp.name = text
                    island.cache(this.rosePlugin)

                    val placeholders = StringPlaceholders.builder("setting", "Warp Name")
                        .addPlaceholder("value", text)
                        .build()

                    this.manager.sendMembersMessage(island, "island-warp-settings-changed", placeholders)
                    return@onComplete AnvilGUI.Response.close()
                }
                .open(player)
        }

        this.put(gui, "warp-icon", player, StringPlaceholders.single("icon", island.warp.icon.type.name.formatEnum())) {
            if (member.role == Role.MEMBER) {
                this.rosePlugin.send(player, "island-no-permission")
                gui.close(player)
                return@put
            }

            val item = player.inventory.itemInMainHand.clone()

            if (item.type.isAir) {
                this.rosePlugin.send(player, "island-warp-icon-invalid")
                return@put
            }

            island.warp.icon = item.clone()
            island.cache(this.rosePlugin)
            val placeholders = StringPlaceholders.builder("setting", "Warp Icon")
                .addPlaceholder("value", item.type.name.formatEnum())
                .build()


            this.manager.sendMembersMessage(island, "island-warp-settings-changed", placeholders)
            gui.close(player)
        }

        this.put(gui, "warp-category", player, StringPlaceholders.single("category", island.warp.category.names.map { it.formatEnum() }.format())) {
            if (member.role == Role.MEMBER) {
                this.rosePlugin.send(player, "island-no-permission")
                gui.close(player)
                return@put
            }

            this.rosePlugin.getManager<MenuManager>()[WarpCategoryGUI::class].openMenu(member, island)
        }

        this.put(gui, "warp-location", player, StringPlaceholders.single("location", island.warp.location.format())) {
            if (member.role == Role.MEMBER) {
                this.rosePlugin.send(player, "island-no-permission")
                gui.close(player)
                return@put
            }

            val placeholders = StringPlaceholders.builder("setting", "Warp Location")
                .addPlaceholder("value", island.warp.location.format())
                .build()

            island.warp.location = player.location
            island.cache(this.rosePlugin)
            this.manager.sendMembersMessage(island, "island-warp-settings-changed", placeholders)

            gui.close(player)
        }

    }

    override val menuName: String
        get() = "warp-settings"

    override val defaultValues: Map<String, Any>
        get() = mapOf(
            "#0" to "GUI Settings",
            "gui-settings.title" to "Island Settings",
            "gui-settings.rows" to 3,

            "#1" to "Border Item",
            "border-item.enabled" to true,
            "border-item.material" to Material.BLACK_STAINED_GLASS_PANE.toString(),
            "border-item.name" to " ",
            "border-item.slots" to listOf("0-26"),

            "#2" to "Back Item",
            "back-item.enabled" to true,
            "back-item.material" to Material.OAK_SIGN.toString(),
            "back-item.name" to "#a6b2fc&lGo Back",
            "back-item.lore" to listOf(
                " &f| &7Click to go back",
                " &f| &7to the main page"
            ),
            "back-item.slot" to 10,
            "back-item.texture" to "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==",

            "#3" to "Warp Name",
            "warp-name.enabled" to true,
            "warp-name.material" to Material.ANVIL.toString(),
            "warp-name.name" to "#a6b2fc&lWarp Name &f| #a6b2fc%name%",
            "warp-name.lore" to listOf(
                " &f| &7Click to change your",
                " &f| &7current island warp name",
                " &f| ",
                " &f| &7Requires #a6b2fcAdmin &7role!"
            ),
            "warp-name.slot" to 13,

            "#4" to "Warp Icon",
            "warp-icon.enabled" to true,
            "warp-icon.material" to Material.ITEM_FRAME.toString(),
            "warp-icon.name" to "#a6b2fc&lWarp Icon &f| #a6b2fc%icon%",
            "warp-icon.lore" to listOf(
                " &f| &7Click to change your",
                " &f| &7current island warp icon",
                " &f| ",
                " &f| &7Requires #a6b2fcAdmin &7role!"
            ),
            "warp-icon.slot" to 14,

            "#5" to "Warp Category",
            "warp-category.enabled" to true,
            "warp-category.material" to Material.NAME_TAG.toString(),
            "warp-category.name" to "#a6b2fc&lWarp Category &f| #a6b2fc%category%",
            "warp-category.lore" to listOf(
                " &f| &7Click to change your",
                " &f| &7current island warp category",
                " &f| ",
                " &f| &7Requires #a6b2fcAdmin &7role!"
            ),
            "warp-category.slot" to 15,

            "#6" to "Warp Location",
            "warp-location.enabled" to true,
            "warp-location.material" to Material.COMPASS.toString(),
            "warp-location.name" to "#a6b2fc&lWarp Location &f| #a6b2fc%location%",
            "warp-location.lore" to listOf(
                " &f| &7Click to change your",
                " &f| &7current island warp location",
                " &f| ",
                " &f| &7Requires #a6b2fcAdmin &7role!"
            ),
            "warp-location.slot" to 16,

            )

}