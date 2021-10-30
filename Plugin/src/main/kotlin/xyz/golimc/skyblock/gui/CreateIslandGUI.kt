package xyz.golimc.skyblock.gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import xyz.golimc.skyblock.SkyblockPlugin
import xyz.golimc.skyblock.manager.DataManager
import xyz.golimc.skyblock.manager.IslandManager
import xyz.golimc.skyblock.manager.WorldManager
import xyz.golimc.skyblock.util.getManager
import xyz.oribuin.gui.Item
import xyz.oribuin.gui.PaginatedGui
import xyz.oribuin.orilibrary.util.HexUtils.colorify

class CreateIslandGUI(private val plugin: SkyblockPlugin) {

    fun create(player: Player) {
        val pageSlots = mutableListOf<Int>()
        for (i in 9..26)
            pageSlots.add(i)

        val gui = PaginatedGui(36, "Choose an island.", pageSlots)

        // Stop people from yoinking items out the gui.
        gui.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }

        // Stop people from putting stuff in the gui.
        gui.setPersonalClickAction { gui.defaultClickFunction.accept(it) }

        for (i in 0..8)
            gui.setItem(i, Item.filler(Material.GRAY_STAINED_GLASS_PANE))

        for (i in 27..35)
            gui.setItem(i, Item.filler(Material.GRAY_STAINED_GLASS_PANE))

        if (gui.page - 1 == gui.prevPage) {
            gui.setItem(29, Item.Builder(Material.PAPER).setName(colorify("#a6b2fc&lPrevious Page")).create()) { gui.previous(it.whoClicked as Player) }
        }

        if (gui.page + 1 == gui.nextPage) {
            gui.setItem(33, Item.Builder(Material.PAPER).setName(colorify("#a6b2fc&lNext Page")).create()) { gui.next(it.whoClicked as Player) }
        }

        gui.setItem(31, Item.Builder(Material.SPRUCE_SIGN).setName(colorify("#a6b2fc&lChoose Your Island")).setLore(infoLore).create()) {}

        val islandManager = this.plugin.getManager<IslandManager>()
        val member = this.plugin.getManager<DataManager>().getMember(player.uniqueId)

        this.plugin.getManager<WorldManager>().schematics.forEach { (_, u) ->
            gui.addPageItem(Item.Builder(u.icon).setName(colorify(u.displayName)).setLore(u.lore.map { colorify(it) }).create()) { islandManager.makeIsland(member, u) }
        }

        gui.open(player)
    }

    private val infoLore: List<String>
        get() = mutableListOf(
            colorify("&f | &7Click to choose your"),
            colorify("&f | &7starting island design!"),
            "",
            colorify("#dd464cWarning! You &lcannot #dd464crevert this!")
        )
}