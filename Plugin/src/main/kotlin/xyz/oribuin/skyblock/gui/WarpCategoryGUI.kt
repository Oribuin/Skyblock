package xyz.oribuin.skyblock.gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import xyz.oribuin.gui.Gui
import xyz.oribuin.gui.Item
import xyz.oribuin.orilibrary.util.StringPlaceholders
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.island.Warp
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.util.*

class WarpCategoryGUI(private val plugin: SkyblockPlugin, private val island: Island) {

    private val data = this.plugin.getManager<DataManager>()
    private lateinit var activeCategory: Warp.Category

    fun create(member: Member) {
        this.activeCategory = island.warp.category
        val player = member.offlinePlayer.player ?: return

        val gui = Gui(27, "Island Category")
        gui.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }

        gui.setPersonalClickAction { gui.defaultClickFunction.accept(it) }

        gui.setCloseAction {
            if (this.activeCategory != island.warp.category) {
                island.warp.category = this.activeCategory
                data.saveIsland(island)

                val placeholders = StringPlaceholders.builder("setting", "Category")
                    .add("player", it.player.name)
                    .add("value", this.activeCategory.name.formatEnum())
                    .build()

                this.sendSettingMessage(placeholders)
                return@setCloseAction
            }
        }

        gui.setItems(numRange(0, 26), Item.filler(Material.BLACK_STAINED_GLASS_PANE))
        gui.setItem(10, Item.Builder(Material.OAK_SIGN).setName("#a6b2fc&lWarp Category".color()).setLore(infoLore).create()) {}

        this.setItems(gui)
        gui.open(player)
    }

    private fun setItems(gui: Gui) {
        Warp.Category.values().forEach {

            val desc = it.desc.toMutableList()
            desc.add(" &f|")
            desc.add(" &f| &7Click to switch category")

            val item = Item.Builder(it.icon)
                .setName("#a6b2fc&l${it.name.formatEnum()}".color())
                .setLore(desc.color())
                .glow(this.activeCategory == it)
                .create()

            gui.setItem(it.slot, item) { _ ->
                this.activeCategory = it
                this.setItems(gui)
                gui.update()
            }
        }
    }

    private fun sendSettingMessage(placeholders: StringPlaceholders) {
        island.members.mapNotNull { it.offlinePlayer.player }
            .forEach { this.plugin.send(it, "changed-warp", placeholders) }
    }

    private val infoLore: List<String>
        get() = listOf(
            " &f| &7Click on icons to change",
            " &f| &7your island warp category!",
            " &f|",
            "&f | &7This allows users to find",
            "&f | &7your warp easier!"
        ).color()
}