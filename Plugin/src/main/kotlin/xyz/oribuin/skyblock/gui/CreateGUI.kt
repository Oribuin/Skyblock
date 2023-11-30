package xyz.oribuin.skyblock.gui

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.utils.HexUtils.colorify
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.Material
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.manager.WorldManager
import xyz.oribuin.skyblock.util.ItemBuilder
import xyz.oribuin.skyblock.util.color
import xyz.oribuin.skyblock.util.getManager

class CreateGUI(rosePlugin: RosePlugin) : PluginGUI(rosePlugin) {

    private val manager = this.rosePlugin.getManager<IslandManager>()
    private val worldManager = this.rosePlugin.getManager<WorldManager>()

    fun openMenu(member: Member) {
        val player = member.onlinePlayer ?: return
        val gui = this.createPagedGUI(player)

        this.put(gui, "border-item", player)
        this.put(gui, "next-page", player) { gui.next() }
        this.put(gui, "island-info", player) {}
        this.put(gui, "previous-page", player) { gui.previous() }
        this.addExtraItems(gui, player)

        this.worldManager.schematics.forEach { (_, schem) ->
            val item = GuiItem(
                ItemBuilder(schem.icon)
                    .name(colorify(schem.displayName))
                    .lore(schem.lore.color())
                    .build()
            )

            item.setAction { this.manager.makeIsland(member, schem) }
            gui.addItem(item)
        }

        gui.open(player)

    }

    override val defaultValues: Map<String, Any>
        get() = mapOf(
            "#0" to "GUI Settings",
            "gui-settings.title" to "Choose an island.",
            "gui-settings.rows" to 4,

            "#1" to "Previous Page",
            "previous-page.name" to "#a6b2fc&lPrevious Page",
            "previous-page.lore" to listOf(" &f| #a6b2fcLeft Click&7 to go to the previous page."),
            "previous-page.material" to Material.PAPER.toString(),
            "previous-page.glow" to true,

            "#2" to "Next Page",
            "next-page.name" to "#a6b2fc&lNext Page",
            "next-page.lore" to listOf(" &f| #a6b2fcLeft Click&7 to go to the next page."),
            "next-page.material" to Material.PAPER.toString(),
            "next-page.glow" to true,

            "#3" to "Island Info",
            "island-info.name" to "#a6b2fc&lChoose Your Island",
            "island-info.lore" to listOf(
                " &f| &7Click to choose your",
                " &f| &7starting island design!",
                "",
                "#dd464cWarning! You &lcannot #dd464crevert this!"
            ),

            "#4" to "Border Item",
            "border-item.enabled" to true,
            "border-item.name" to " ",
            "border-item.material" to Material.BLACK_STAINED_GLASS_PANE.toString(),
            "border-item.slots" to listOf("0-8", "27-35")

        )
    override val menuName: String
        get() = "create-gui"
}