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
import xyz.oribuin.skyblock.nms.NMSAdapter
import xyz.oribuin.skyblock.util.color
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.numRange

class WarpDescGUI(private val plugin: SkyblockPlugin, private val island: Island, player: Player) {

    private var description = mutableMapOf<Int, String>()
    private var descriptionChanged: Boolean = false
    private val data = this.plugin.getManager<DataManager>()
    private var usingAnvil = false

    init {
        val gui = PaginatedGui(27, "Warp Description", numRange(9, 18))
        gui.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }

        gui.setCloseAction {
            if (!this.usingAnvil)
                data.saveIsland(island)
        }

        var id = 0
        this.island.warp.desc.text.forEach { description[++id] = it }

        gui.setItems(numRange(0, 8), Item.filler(Material.BLACK_STAINED_GLASS_PANE))
        gui.setItems(numRange(18, 26), Item.filler(Material.BLACK_STAINED_GLASS_PANE))

        val addLineLore = listOf(
            " &f| &7Click to add a new".color(),
            " &f| &7line to the description.".color(),
            " &f|".color(),
            " &f| &79 Lines Max".color()
        )

        var item = Item.Builder(Material.PLAYER_HEAD).setName("#a6b2fc&lAdd Line".color())
            .setLore(addLineLore)
            .setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0=")
            .create()

        item = NMSAdapter.handler.setInt(item, "CustomModelData", 1) // for a resource pack i might make in the future

        gui.setItem(2, item) { this.addLine(gui, player) }

        val infoLore = listOf(
            " &f| &7Click the plus to".color(),
            " &f| &7add a new line.".color(),
            " &f|".color(),
            " &f| &7Left-Click on a line ".color(),
            " &f| &7to change the text.".color(),
            " &f|".color(),
            " &f| &7Right-Click on a line".color(),
            " &f| &7to remove it.".color()
        )

        gui.setItem(4, Item.Builder(Material.OAK_SIGN).setName("#a6b2fc&lGeneral Info".color()).setLore(infoLore).create()) {}


        gui.setItem(6, Item.Builder(Material.PLAYER_HEAD).setName("#a6b2fc&lGo Back".color()).setLore(" &f| &7Click to go back".color(), " &f| &7to the main page.".color()).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==").create()) {
            (it.whoClicked as Player).chat("/island warp settings")
        }

        this.loadDescription(gui)
        gui.open(player)
    }

    private fun addLine(gui: PaginatedGui, player: Player) {
        val stepNumber = description.size + 1
        if (description.size == 9)
            return

        this.usingAnvil = true
        AnvilGUI.Builder()
            .plugin(this.plugin)
            .title("Line #$stepNumber")
            .itemLeft(Item.filler(Material.PAPER).item)
            .text("New Line")
            .preventClose()
            .onClose { gui.open(player) }
            .onComplete { _, text ->
                this.descriptionChanged = true
                this.usingAnvil = false

                this.description[stepNumber] = text
                this.loadDescription(gui)
                gui.update()
                return@onComplete AnvilGUI.Response.close()
            }
            .open(player)
    }

    private fun changeLine(id: Int, gui: PaginatedGui, player: Player) {
        val action = this.description[id] ?: return
        this.usingAnvil = true

        AnvilGUI.Builder()
            .plugin(this.plugin)
            .title("Change Line #$id")
            .itemLeft(Item.filler(Material.PAPER).item)
            .text(action)
            .preventClose()
            .onClose { gui.open(player) }
            .onComplete { _, text ->
                this.description[id] = text
                this.descriptionChanged = true
                this.usingAnvil = false

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
                    this.descriptionChanged = true

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