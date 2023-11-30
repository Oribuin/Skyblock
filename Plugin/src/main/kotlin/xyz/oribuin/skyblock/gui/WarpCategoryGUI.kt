package xyz.oribuin.skyblock.gui

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.utils.StringPlaceholders
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.Material
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.island.Warp
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.manager.MenuManager
import xyz.oribuin.skyblock.util.ItemBuilder
import xyz.oribuin.skyblock.util.cache
import xyz.oribuin.skyblock.util.color
import xyz.oribuin.skyblock.util.format
import xyz.oribuin.skyblock.util.getManager

class WarpCategoryGUI(rosePlugin: RosePlugin) : PluginGUI(rosePlugin) {

    private val categories = mutableMapOf<Int, Warp.Category>()
    private val manager = this.rosePlugin.getManager<IslandManager>()

    fun openMenu(member: Member, island: Island) {
        val player = member.onlinePlayer ?: return

        val gui = this.createGUI(player)
        gui.setCloseGuiAction {
            val cachedCategories = this.categories[island.key] ?: return@setCloseGuiAction

            island.warp.category = cachedCategories
            island.cache(this.rosePlugin)

            val placeholders = StringPlaceholders.builder("setting", "Warp Category")
                .add("value", island.warp.category.formatted())
                .build()

            this.manager.sendMembersMessage(island, "island-warp-settings-changed", placeholders)
        }

        this.put(gui, "border-item", player)
        this.put(gui, "category-info", player) {
            this.rosePlugin.getManager<MenuManager>()[WarpSettingsGUI::class].openMenu(member)
        }

        this.addExtraItems(gui, player)


        gui.open(player)
        this.setupCategories(gui, island)
    }

    private fun setupCategories(gui: Gui, island: Island) {
        val activeCategories = island.warp.category.clone()
        Warp.Category.Type.values().forEach {

            val description = it.desc.toMutableList()
            description.addAll(listOf(" &f|", " &f| &7Click to switch category"))

            val item = ItemBuilder(it.icon)
                .name("#a6b2fc&l${it.format()}".color())
                .lore(description.color())
                .glow(activeCategories.types.contains(it.name))
                .build()

            gui.setItem(it.slot, GuiItem(item) { _ ->

                if (!activeCategories.types.remove(it.name))
                    activeCategories.types.add(it.name)

                this.categories[island.key] = activeCategories
                this.setupCategories(gui, island)
            })
        }

        gui.update()
    }

    override val menuName: String
        get() = "warp-category"

    override val defaultValues: Map<String, Any>
        get() = mapOf(
            "#0" to "GUI Settings",
            "gui-settings.title" to "Island Category",
            "gui-settings.rows" to 3,

            "#1" to "Border Item",
            "border-item.enabled" to true,
            "border-item.material" to Material.BLACK_STAINED_GLASS_PANE.toString(),
            "border-item.name" to " ",
            "border-item.slots" to listOf("0-26"),

            "#2" to "Category Info",
            "category-info.enabled" to true,
            "category-info.slot" to 10,
            "category-info.material" to Material.OAK_SIGN.toString(),
            "category-info.name" to "#a6b2fc&lCategory Info",
            "category-info.lore" to listOf(
                "&f | &7Click on icons to change",
                "&f | &7your island warp category!",
                "&f |",
                "&f | &7This allows users to find",
                "&f | &7your warp easier!",
                "&f |",
                "&f | #a6b2fcLeft-Click &7to go back."
            )
        )
}