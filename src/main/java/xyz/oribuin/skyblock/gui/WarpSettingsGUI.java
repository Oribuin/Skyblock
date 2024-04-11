package xyz.oribuin.skyblock.gui

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.utils.StringPlaceholders
import dev.triumphteam.gui.guis.Gui
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import xyz.oribuin.skyblock.island.member.Member
import xyz.oribuin.skyblock.island.member.Member.Role
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.cache
import xyz.oribuin.skyblock.util.format
import xyz.oribuin.skyblock.util.formatEnum
import xyz.oribuin.skyblock.util.getMenu
import xyz.oribuin.skyblock.util.send

class WarpSettingsGUI(rosePlugin: RosePlugin) : xyz.oribuin.skyblock.gui.PluginGUI(rosePlugin) {

    private val manager = this.rosePlugin.getManager<skyblock.manager.IslandManager>()

    fun openMenu(member: Member) {
        val island = member.getIsland(this.rosePlugin) ?: return
        val player = member.onlinePlayer ?: return

        val gui = this.createGUI(player)
        this.put(gui, "border-item", player)
        this.put(gui, "back-item", player) { this.rosePlugin.getMenu(xyz.oribuin.skyblock.gui.WarpsGUI::class).openMenu(member) }
        this.setSettings(gui, member, island)
        this.addExtraItems(gui, player)

        gui.open(player)
    }

    private fun setSettings(gui: Gui, member: Member, island: xyz.oribuin.skyblock.island.Island) {
        val player = member.onlinePlayer ?: return

        this.put(gui, "warp-name", player, StringPlaceholders.of("name", island.warp.name)) {
            if (member.role == Role.MEMBER) {
                this.rosePlugin.send(player, "island-no-permission")
                gui.close(player)
                return@put
            }

            AnvilGUI.Builder()
                .plugin(this.rosePlugin)
                .title("Enter a new warp name")
                .text(island.warp.name)
                .itemLeft(skyblock.util.ItemBuilder.filler(Material.NAME_TAG))
                .onClick { slot, snapshot ->
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return@onClick listOf(AnvilGUI.ResponseAction.close())
                    }

                    val text = snapshot.text
                    if (text.isEmpty() || text.equals(island.warp.name, ignoreCase = true)) {
                        return@onClick listOf(AnvilGUI.ResponseAction.close())
                    }

                    island.warp.name = text
                    island.cache(this.rosePlugin)

                    this.manager.sendMembersMessage(
                        island, "island-warp-settings-changed", StringPlaceholders.of(
                            "setting", "Warp Name",
                            "value", text
                        )
                    )
                    return@onClick listOf(AnvilGUI.ResponseAction.close())
                }
                .open(player)
        }

        this.put(gui, "warp-icon", player, StringPlaceholders.of("icon", island.warp.icon.type.name.formatEnum())) {
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
                .add("value", item.type.name.formatEnum())
                .build()


            this.manager.sendMembersMessage(island, "island-warp-settings-changed", placeholders)
            gui.close(player)
        }

        this.put(
            gui,
            "warp-category",
            player,
            StringPlaceholders.of("category", island.warp.category.formatted())
        ) {
            if (member.role == Role.MEMBER) {
                this.rosePlugin.send(player, "island-no-permission")
                gui.close(player)
                return@put
            }

            this.rosePlugin.getManager<skyblock.manager.MenuManager>()[xyz.oribuin.skyblock.gui.WarpCategoryGUI::class].openMenu(member, island)
        }

        this.put(gui, "warp-location", player, StringPlaceholders.of("location", island.warp.location.format())) {
            if (member.role == Role.MEMBER) {
                this.rosePlugin.send(player, "island-no-permission")
                gui.close(player)
                return@put
            }

            val placeholders = StringPlaceholders.builder("setting", "Warp Location")
                .add("value", island.warp.location.format())
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
                " &f|",
                " &f| &7Hold the item you want",
                " &f| &7to be the warp icon",
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