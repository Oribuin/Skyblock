package dev.oribuin.skyblock.gui.impl.warp;

public class WarpCategoryGUI {
    //     private val categories = mutableMapOf < Int, skyblock.island.Warp.Category > ()
    //    private val manager = this.rosePlugin.getManager < skyblock.manager.IslandManager > ()
    //
    //    fun openMenu (member:Member, island:dev.oribuin.skyblock.island.Island){
    //        val player = member.onlinePlayer ?:return
    //
    //                val gui = this.createGUI(player)
    //        gui.setCloseGuiAction {
    //            val cachedCategories = this.categories[island.key] ?:return @setCloseGuiAction
    //
    //                    island.warp.category = cachedCategories
    //            island.cache(this.rosePlugin)
    //
    //            val placeholders = StringPlaceholders.builder("setting", "Warp Category")
    //                    .add("value", island.warp.category.formatted())
    //                    .build()
    //
    //            this.manager.sendMembersMessage(island, "island-warp-settings-changed", placeholders)
    //        }
    //
    //        this.put(gui, "border-item", player)
    //        this.put(gui, "category-info", player) {
    //            this.rosePlugin.getManager < skyblock.manager.MenuManager > ()[dev.oribuin.skyblock.gui.WarpSettingsGUI:: class].openMenu(member)
    //        }
    //
    //        this.addExtraItems(gui, player)
    //
    //
    //        gui.open(player)
    //        this.setupCategories(gui, island)
    //    }
    //
    //    private fun setupCategories (gui:Gui, island:dev.oribuin.skyblock.island.Island){
    //        val activeCategories = island.warp.category.clone()
    //        skyblock.island.Warp.Category.Type.values().forEach {
    //
    //            val description = it.desc.toMutableList()
    //            description.addAll(listOf(" <white>|", " <white>| <gray>Click to switch category"))
    //
    //            val item = skyblock.util.ItemBuilder(it.icon)
    //                    .name(<#a6b2fc><bold>${it.format()}".color())
    //                    .lore(description.color())
    //                    .glow(activeCategories.types.contains(it.name))
    //                    .build()
    //
    //            gui.setItem(it.slot, GuiItem(item) {
    //                _ ->
    //
    //                if (!activeCategories.types.remove(it.name))
    //                    activeCategories.types.add(it.name)
    //
    //                this.categories[island.key] = activeCategories
    //                this.setupCategories(gui, island)
    //            })
    //        }
    //
    //        gui.update()
    //    }
    //
    //    override val String
    //    get() = "warp-category"
    //
    //    override val Map<String, Any>
    //    get() = mapOf(
    //            "#0"to"GUI Settings",
    //            "gui-settings.title"to"Island Category",
    //            "gui-settings.rows"to 3,
    //
    //            "#1"to"Border Item",
    //            "border-item.enabled"to true,
    //            "border-item.material" to Material.BLACK_STAINED_GLASS_PANE.toString(),
    //            "border-item.name" to " ",
    //            "border-item.slots" to listOf ("0-26"),
    //
    //            "#2" to "Category Info",
    //            "category-info.enabled" to true,
    //            "category-info.slot" to 10,
    //            "category-info.material" to Material.OAK_SIGN.toString(),
    //            "category-info.name" to <#a6b2fc><bold>Category Info",
    //            "category-info.lore" to listOf (
    //            "<white> | <gray>Click on icons to change",
    //            "<white> | <gray>your island warp category!",
    //            "<white> |",
    //            "<white> | <gray>This allows users to find",
    //            "<white> | <gray>your warp easier!",
    //            "<white> |",
    //            "<white> | #a6b2fcLeft-Click <gray>to go back."
    //            )
    //        )
}
