package xyz.oribuin.skyblock.gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.nms.BorderColor
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.gui.Gui
import xyz.oribuin.gui.Item
import xyz.oribuin.orilibrary.util.HexUtils.colorify

class BorderGUI(private val plugin: SkyblockPlugin) {

    private val data = plugin.getManager<DataManager>()
    private lateinit var activeColor: BorderColor

    fun create(player: Player) {

        val member = data.getMember(player.uniqueId)
        activeColor = member.border

        val gui = Gui(27, "Border Color: " + activeColor.name.lowercase().replaceFirstChar { it.uppercase() })

        // Stop people from yoinking items out the gui.
        gui.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }

        // Stop people from putting stuff in the gui.
        gui.setPersonalClickAction { gui.defaultClickFunction.accept(it) }

        gui.setCloseAction {
            if (activeColor != member.border) {
                member.border = activeColor
                data.saveMember(member)
            }

            val islandManager = this.plugin.getManager<IslandManager>()
            val island = islandManager.getIslandFromLoc(it.player.location) ?: return@setCloseAction
            islandManager.createBorder(member, island)
        }

        for (i in 0..26)
            gui.setItem(i, Item.filler(Material.GRAY_STAINED_GLASS_PANE))

        gui.setItem(10, Item.Builder(Material.SPRUCE_SIGN).setName(colorify("#a6b2fc&lBorder Color")).setLore(infoLore).create()) {}
        this.setBorderItems(gui)

        gui.open(player)
    }

    private fun setBorderItems(gui: Gui) {
        gui.setItem(
            12, Item.Builder(Material.RED_DYE)
                .setName(colorify("#ff6961&lRed Border"))
                .glow(activeColor == BorderColor.RED)
                .setLore(borderLore).create()
        ) {
            activeColor = BorderColor.RED
            setBorderItems(gui)
            gui.update()
        }
        gui.setItem(
            13, Item.Builder(Material.LIME_DYE)
                .setName(colorify("#77dd77&lGreen Border"))
                .glow(activeColor == BorderColor.GREEN)
                .setLore(borderLore).create()
        ) {
            activeColor = BorderColor.GREEN
            setBorderItems(gui)
            gui.update()
        }

        gui.setItem(
            14, Item.Builder(Material.LIGHT_BLUE_DYE)
                .setName(colorify("#417cfc&lBlue Border"))
                .glow(activeColor == BorderColor.BLUE)
                .setLore(borderLore)
                .create()
        ) {
            activeColor = BorderColor.BLUE
            setBorderItems(gui)
            gui.update()
        }

        // reset the color
        gui.setItem(
            16, Item.Builder(Material.GRAY_DYE)
                .setName(colorify("#a6b2fc&lInvisible Border"))
                .glow(activeColor == BorderColor.OFF)
                .setLore(borderLore)
                .create()
        ) {
            activeColor = BorderColor.OFF
            setBorderItems(gui)
            gui.update()
        }
    }

    private val infoLore: List<String>
        get() = mutableListOf(
            colorify("&f | &7Click on the dyes change"),
            colorify("&f | &7your personal border color!"),
            "",
            colorify("&f | &7This is only visible to you.")
        )

    private val borderLore: List<String>
        get() = mutableListOf(
            colorify("&f | &7Click to change your"),
            colorify("&f | &7personal island border"),
            colorify("&f | &7to this color!")
        )
}