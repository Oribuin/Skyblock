package xyz.oribuin.skyblock.gui

import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import xyz.oribuin.gui.Gui
import xyz.oribuin.gui.Item
import xyz.oribuin.orilibrary.util.HexUtils.colorify
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.numRange
import xyz.oribuin.skyblock.util.send

class SettingsGUI(private val plugin: SkyblockPlugin, private val island: Island) {

    private val data = this.plugin.getManager<DataManager>()
    private val islandManager = this.plugin.getManager<IslandManager>()

    fun create(member: Member) {
        val player = member.offlinePlayer.player ?: return

        val gui = Gui(27, "Island Settings")
        // Stop people from yoinking items out the gui.
        gui.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }
        // Stop people from putting stuff in the gui.
        gui.setPersonalClickAction { gui.defaultClickFunction.accept(it) }
        gui.setItems(numRange(0, 26), Item.filler(Material.GRAY_STAINED_GLASS_PANE))

        gui.setItem(10, Item.Builder(Material.PLAYER_HEAD).setName(colorify("#a6b2fc&lGo Back")).setLore(colorify(" &f| &7Click to go back"), colorify(" &f| &7to the main page.")).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==").create()) {
            (it.whoClicked as Player).chat("/island")
        }

        this.setSettings(gui, member)

        gui.open(player)

    }

    private fun setSettings(gui: Gui, member: Member) {
        val settings = island.settings

        gui.setItem(12, Item.Builder(Material.NAME_TAG).setName("#a6b2fc&lIsland Name").setLore(colorify(" &f| &7Click to change your"), colorify(" &f| &7current island name."), colorify(" &f| "), colorify(" &f| &7Requires #a6b2fcAdmin &7role!")).create()) {
            if (member.role == Member.Role.MEMBER) {
                this.plugin.send(it.whoClicked, "invalid-island-role")
                return@setItem
            }


        }

        gui.update()
    }

    /**
     * Edit the island's name using an anvil gui
     *
     * @param gui The GUI to go back to
     * @param player The player changing the name.
     */
    private fun editIslandName(gui: Gui, player: Player) {

        AnvilGUI.Builder()
            .plugin(this.plugin)
            .title("Island Name")
            .title(island.settings.name)
            .itemLeft(Item.filler(Material.NAME_TAG).item)
            .onClose { gui.open(player) }
            .onComplete { user, text ->
                return@onComplete AnvilGUI.Response.close()
            }
            .open(player)
    }

}