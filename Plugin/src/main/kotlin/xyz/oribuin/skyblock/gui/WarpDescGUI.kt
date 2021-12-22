package xyz.oribuin.skyblock.gui

import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import xyz.oribuin.gui.Item
import xyz.oribuin.gui.PaginatedGui
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Warp
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.color
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.numRange

class WarpDescGUI(private val plugin: SkyblockPlugin, private val island: Island, player: Player) {

    private var description = mutableMapOf<Int, String>()
    private val data = this.plugin.getManager<DataManager>()

    init {
        val gui = PaginatedGui(27, "Warp Description", numRange(9, 18))
        gui.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }

        var id = 0
        this.island.warp.desc.text.forEach { description[++id] = it }

        gui.setItems(numRange(0, 8), Item.filler(Material.BLACK_STAINED_GLASS_PANE))
        gui.setItems(numRange(18, 26), Item.filler(Material.BLACK_STAINED_GLASS_PANE))

        gui.setItem(4, Item.filler(Material.GREEN_BANNER).item) {
            addLine(gui, player)
        }

        this.loadDescription(gui)
        gui.open(player)
    }

    private fun addLine(gui: PaginatedGui, player: Player) {
        val stepNumber = description.size + 1
        if (description.size == 9)
            return

        AnvilGUI.Builder()
            .plugin(this.plugin)
            .title("Line #$stepNumber")
            .itemLeft(Item.filler(Material.PAPER).item)
            .text("New Line")
            .preventClose()
            .onClose { gui.open(player) }
            .onComplete { _, text ->
                this.description[stepNumber] = text
                this.loadDescription(gui)
                gui.update()
                return@onComplete AnvilGUI.Response.close()
            }
            .open(player)
    }

    private fun changeLine(id: Int, gui: PaginatedGui, player: Player) {
        val action = this.description[id] ?: return
        AnvilGUI.Builder()
            .plugin(this.plugin)
            .title("Change Step #$id")
            .itemLeft(Item.filler(Material.PAPER).item)
            .text(action)
            .preventClose()
            .onClose { gui.open(player) }
            .onComplete { _, text ->
                this.description[id] = text
                this.loadDescription(gui)
                gui.update()
                return@onComplete AnvilGUI.Response.close()
            }
            .open(player)
    }

    private fun loadDescription(gui: PaginatedGui) {
        gui.pageItems.clear()
        island.warp.desc = Warp.Desc(this.description.values.toMutableList())
        data.islandCache[island.key] = island


        this.description.forEach { (t, u) ->
            val item = Item.Builder(Material.PAPER)
                .setName("#a6b2fc&lLine #$t".color())
                .setLore(" &f| $u".color())
                .create()

            gui.addPageItem(item) {
                if (it.isLeftClick)
                    this.changeLine(t, gui, it.whoClicked as Player)

                if (it.isRightClick) {
                    this.description.remove(t)
                    this.description = shiftMap(this.description)
                    this.loadDescription(gui)
                    gui.update()
                }
            }
        }
    }

    private fun shiftMap(map: Map<Int, String>): MutableMap<Int, String> {
        var currentNumber = 0
        val newMap = mutableMapOf<Int, String>()
        map.toSortedMap { o1, o2 -> o1.compareTo(o2) }.forEach {
            currentNumber++
            newMap[currentNumber] = it.value
        }
        return newMap
    }

}