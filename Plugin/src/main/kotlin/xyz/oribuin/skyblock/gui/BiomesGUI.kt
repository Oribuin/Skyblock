package xyz.oribuin.skyblock.gui

import org.apache.commons.lang.WordUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.ClickType
import xyz.oribuin.gui.Item
import xyz.oribuin.gui.PaginatedGui
import xyz.oribuin.orilibrary.util.HexUtils.colorify
import xyz.oribuin.orilibrary.util.StringPlaceholders
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.numRange
import xyz.oribuin.skyblock.util.send

class BiomesGUI(private val plugin: SkyblockPlugin) {

    private val data = this.plugin.getManager<DataManager>()
    private val islandManager = this.plugin.getManager<IslandManager>()

    fun create(player: Player, island: Island) {
        val gui = PaginatedGui(36, "Island Biomes", numRange(9, 26))
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

        islandManager.biomeMap.toMutableMap().toSortedMap { o1, o2 -> o1.name.compareTo(o2.name) }.forEach { (t, u) ->
            val biomeName = WordUtils.capitalizeFully(t.name.lowercase().replace("_", " "))
            val item = Item.Builder(u.icon)
                .setName(colorify("#a6b2fc&l$biomeName"))
                .setLore(colorify(" &f| #a6b2fcShift-Left Click&7 to change"), colorify(" &f| &7your island to this biome."), colorify(" &f|"), colorify(" &f| &7Cost: #a6b2fc$${u.cost}"))
                .glow(island.settings.biome == t)
                .create()

            gui.addPageItem(item) {
                if (it.click != ClickType.SHIFT_LEFT)
                    return@addPageItem

                val whoClicked = it.whoClicked as Player

                if (island.settings.biome == t)
                    return@addPageItem

                if (!this.plugin.vault.has(whoClicked, u.cost)) {
                    this.plugin.send(whoClicked, "not-enough-money", StringPlaceholders.single("cost", u.cost))
                    player.closeInventory()
                    return@addPageItem
                }

                island.members.mapNotNull { member -> member.offlinePlayer.player }.forEach { member ->
                    this.plugin.send(member, "changed-biome", StringPlaceholders.single("biome", biomeName))
                }

                whoClicked.closeInventory()
                this.plugin.vault.withdrawPlayer(whoClicked, u.cost)

                island.settings.biome = t
                islandManager.setIslandBiome(island)
                this.data.saveIsland(island)
            }
        }

        gui.open(player)
    }

}