package xyz.oribuin.skyblock.gui

import org.bukkit.Bukkit
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
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.MessageManager
import xyz.oribuin.skyblock.util.color
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.numRange

class MembersGUI(private val plugin: SkyblockPlugin) {

    private val msg = this.plugin.getManager<MessageManager>()

    fun create(player: Player, island: Island) {
        val gui = PaginatedGui(27, "Island Members", numRange(9, 26))
        // Stop people from yoinking items out the gui.
        gui.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }

        // Stop people from putting stuff in the gui.
        gui.setPersonalClickAction { gui.defaultClickFunction.accept(it) }

        // Save the island when the gui is closed
        gui.setCloseAction { plugin.getManager<DataManager>().saveIsland(island) }

        gui.setItems(numRange(0, 8), Item.filler(Material.BLACK_STAINED_GLASS_PANE))
        gui.setItems(numRange(18, 26), Item.filler(Material.BLACK_STAINED_GLASS_PANE))

        if (gui.page - 1 == gui.prevPage) {
            gui.setItem(20, Item.Builder(Material.PAPER).setName(colorify("#a6b2fc&lPrevious Page")).create()) { gui.previous(it.whoClicked as Player) }
        }

        if (gui.page + 1 == gui.nextPage) {
            gui.setItem(24, Item.Builder(Material.PAPER).setName(colorify("#a6b2fc&lNext Page")).create()) { gui.next(it.whoClicked as Player) }
        }

        gui.setItem(22, Item.Builder(Material.PLAYER_HEAD).setName("#a6b2fc&lGo Back".color()).setLore(" &f| &7Click to go back".color(), " &f| &7to the main page.".color()).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==").create()) {
            (it.whoClicked as Player).chat("/island")
        }

        val viewer = plugin.getManager<DataManager>().getMember(player.uniqueId)

        this.addMembers(viewer, gui, island)

        gui.open(player)
    }

    private fun addMembers(viewer: Member, gui: PaginatedGui, island: Island) {
        gui.pageItems.clear()
        island.members.sortedBy { x -> x.role.priority }.forEach { member ->
            gui.addPageItem(getPlayer(viewer, member)) {
                if (it.isLeftClick && member.role == Member.Role.MEMBER) {
                    member.role = Member.Role.ADMIN
                } else if (it.isRightClick && member.role == Member.Role.ADMIN)
                    member.role = Member.Role.MEMBER

                this.addMembers(viewer, gui, island)
                // run async or else the vault plugin will screech
                Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable { gui.update() })
            }
        }
    }

    private fun getPlayer(viewer: Member, member: Member): ItemStack {

        val lore = mutableListOf(
            colorify(" &f| &7Rank: #a6b2fc${xyz.oribuin.skyblock.manager.apply(member.offlinePlayer, "%vault_rank_capital%")}"),
            colorify(" &f| &7Role: #a6b2fc${member.role.name.lowercase().replaceFirstChar { it.uppercase() }}"),
        )

        if (viewer.role == Member.Role.OWNER && member.uuid != viewer.uuid) {
            lore.add(colorify(" &f| "))
            lore.add(colorify(" &f| #a6b2fcLeft-Click &7to promote."))
            lore.add(colorify(" &f| #a6b2fcRight-Click &7to demote."))
        }

        return Item.Builder(Material.PLAYER_HEAD)
            .setName(colorify("#a6b2fc&l${member.offlinePlayer.name}"))
            .setOwner(member.offlinePlayer)
            .setLore(lore)
            .create()
    }

}