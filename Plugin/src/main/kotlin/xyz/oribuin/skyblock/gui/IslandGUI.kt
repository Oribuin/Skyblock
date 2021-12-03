package xyz.oribuin.skyblock.gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import xyz.oribuin.gui.Gui
import xyz.oribuin.gui.Item
import xyz.oribuin.orilibrary.util.HexUtils.colorify
import xyz.oribuin.skyblock.SkyblockPlugin

class IslandGUI(private val plugin: SkyblockPlugin) {

    fun create(player: Player) {
        val gui = Gui(27, "Island Panel")

        // Stop people from yoinking items out the gui.
        gui.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }

        // Stop people from putting stuff in the gui.
        gui.setPersonalClickAction { gui.defaultClickFunction.accept(it) }

        for (i in 0..26)
            gui.setItem(i, Item.filler(Material.BLACK_STAINED_GLASS_PANE))

        val homeLore = listOf(colorify(" &f| &7Click to teleport to"), colorify(" &f| &7your island home!"))
        gui.setItem(10, Item.Builder(Material.CYAN_BED).setName(colorify("#a6b2fc&lTeleport Home")).setLore(homeLore).create()) {
            (it.whoClicked as Player).chat("/island go")
        }

        val membersLore = listOf(
            colorify(" &f| &7Click to view all the members"),
            colorify(" &f| &7that are on your island!")
        )
        gui.setItem(13, Item.Builder(Material.PLAYER_HEAD).setName(colorify("#a6b2fc&lMembers")).setLore(membersLore).setOwner(player).create()) {
            (it.whoClicked as Player).chat("/island members")
        }

        val upgradesLore = listOf(
            colorify(" &f| &7Click to view & manage your"),
            colorify(" &f| &7your island upgrades!")
        )
        gui.setItem(14, Item.Builder(Material.BEACON).setName(colorify("#a6b2fc&lUpgrades")).setLore(upgradesLore).create()) {
            (it.whoClicked as Player).chat("/island upgrades")
        }

        val settingsLore = listOf(
            colorify(" &f| &7Click to view & manage your"),
            colorify(" &f| &7your island settings!")
        )
        gui.setItem(15, Item.Builder(Material.REDSTONE).setName(colorify("#a6b2fc&lSettings")).setLore(settingsLore).create()) {
            (it.whoClicked as Player).chat("/island settings")
        }

        val warpLore = listOf(
            colorify(" &f| &7Click to view & manage your"),
            colorify(" &f| &7your island warp!")
        )
        gui.setItem(16, Item.Builder(Material.SPRUCE_SIGN).setName(colorify("#a6b2fc&lWarp")).setLore(warpLore).create()) {
            (it.whoClicked as Player).chat("/island warp")
        }

        gui.open(player)
    }
}