package xyz.oribuin.skyblock.gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import xyz.oribuin.gui.Item
import xyz.oribuin.gui.PaginatedGui
import xyz.oribuin.orilibrary.util.HexUtils.colorify
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.manager.MessageManager
import xyz.oribuin.skyblock.util.getManager

class MembersGUI(private val plugin: SkyblockPlugin) {

    private val msg = this.plugin.getManager<MessageManager>()

    fun create(player: Player, island: Island) {
        val pageSlots = mutableListOf<Int>()
        for (i in 9..26)
            pageSlots.add(i)

        val gui = PaginatedGui(36, "Island Members", pageSlots)
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

        island.members.sortedBy { x -> x.role.priority }.forEach { gui.addPageItem(getPlayer(it)) {} }
        gui.open(player)
    }

    private fun getPlayer(member: Member): ItemStack {

        val lore = mutableListOf(
            colorify(" &f| &7Rank: #a6b2fc${MessageManager.apply(member.offlinePlayer, "%vault_rank_capital%")}"),
            colorify(" &f| &7Role: #a6b2fc${member.role.name.lowercase().replaceFirstChar { it.uppercase() }}"),
        )

        if (member.role == Member.Role.OWNER) {
            lore.add(colorify(" &f| "))
            lore.add(colorify(" &f| #a6b2fcLeft-Click &7to change their role."))
        }

        return Item.Builder(Material.PLAYER_HEAD)
            .setName(colorify("#a6b2fc&l${member.offlinePlayer.name}"))
            .setOwner(member.offlinePlayer)
            .setLore(lore)
            .create()
    }

}