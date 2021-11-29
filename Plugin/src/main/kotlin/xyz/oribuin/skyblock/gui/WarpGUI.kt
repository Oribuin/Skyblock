package xyz.oribuin.skyblock.gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import xyz.oribuin.gui.Item
import xyz.oribuin.gui.PaginatedGui
import xyz.oribuin.orilibrary.util.HexUtils.colorify
import xyz.oribuin.orilibrary.util.StringPlaceholders
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.center
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.numRange

class WarpGUI(private val plugin: SkyblockPlugin) {

    private val data = this.plugin.getManager<DataManager>()
    private val islandManager = this.plugin.getManager<IslandManager>()

    fun create(member: Member) {
        val player = member.offlinePlayer.player ?: return

        val gui = PaginatedGui(45, "Island Warps", numRange(9, 35))
        // Stop people from yoinking items out the gui.
        gui.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }
        gui.setItems(numRange(0, 8), Item.filler(Material.GRAY_STAINED_GLASS_PANE))
        gui.setItems(numRange(18, 26), Item.filler(Material.GRAY_STAINED_GLASS_PANE))

        // Stop people from putting stuff in the gui.
        gui.setPersonalClickAction { gui.defaultClickFunction.accept(it) }
        this.loadWarps(gui, member)
        gui.open(player)
    }

    private fun loadWarps(gui: PaginatedGui, member: Member) {
        gui.pageItems.clear()

        this.data.islandCache.forEach {
            val warp = it.value.warp

            val placeholders = StringPlaceholders.builder("votes", warp.votes)
                .add("category", warp.category.name.lowercase().replaceFirstChar { x -> x.uppercase() })
                .add("visits", warp.visits)
                .add("owner", Bukkit.getOfflinePlayer(it.value.owner).name)

            val lore = mutableListOf(
                " &f| &7Owner: #a6b2fc%owner%",
                " &f| &7Category: #a6b2fc%category%",
                " &f| &7Visits: #a6b2fc%visits%",
                " &f| &7Votes: #a6b2fc%owner%",
                " &f|"
            )

            warp.desc.text.forEach { line -> lore.add(" &f| $line") }

            val item = Item.Builder(warp.icon)
                .setName(colorify("#a6b2fc&l${warp.name}"))
                .setLore(lore.map { s -> colorify(placeholders.apply(s)) })
                .setOwner(Bukkit.getOfflinePlayer(it.value.owner))
                .create()

            gui.addPageItem(item) {
                this.islandManager.teleport(member, warp.location.center())
            }
        }
    }


}