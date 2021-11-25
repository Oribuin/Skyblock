package xyz.oribuin.skyblock.gui

import org.bukkit.entity.Player
import org.bukkit.event.Event
import xyz.oribuin.gui.PaginatedGui
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.numRange

class WarpGUI(private val plugin: SkyblockPlugin) {

    private val data = this.plugin.getManager<DataManager>()
    private val islandManager = this.plugin.getManager<IslandManager>()

    fun create(player: Player) {
        val gui = PaginatedGui(36, "Island Warps", numRange(9, 26))
        // Stop people from yoinking items out the gui.
        gui.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }

        // Stop people from putting stuff in the gui.
        gui.setPersonalClickAction { gui.defaultClickFunction.accept(it) }

        gui.open(player)
    }
}