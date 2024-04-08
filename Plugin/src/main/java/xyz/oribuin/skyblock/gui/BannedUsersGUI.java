package xyz.oribuin.skyblock.gui

import dev.rosewood.rosegarden.RosePlugin
import dev.triumphteam.gui.guis.GuiItem
import dev.triumphteam.gui.guis.PaginatedGui
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.oribuin.skyblock.island.member.Member
import xyz.oribuin.skyblock.island.member.Member.Role
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.color
import xyz.oribuin.skyblock.util.getIsland
import java.text.SimpleDateFormat

class BannedUsersGUI(rosePlugin: RosePlugin) : xyz.oribuin.skyblock.gui.PluginGUI(rosePlugin) {

    private val manager = this.rosePlugin.getManager<skyblock.manager.IslandManager>()
    private val dateFormat = SimpleDateFormat("dd/MMM/yyyy hh:mm:ss z")

    fun openMenu(viewer: Member) {
        val island = viewer.getIsland(this.rosePlugin) ?: return
        val player = viewer.onlinePlayer ?: return

        val gui = this.createPagedGUI(player)
        this.put(gui, "border-item", player)
        this.put(gui, "next-page", player) { gui.next() }
        this.put(gui, "member-info", player) {}
        this.put(gui, "previous-page", player) { gui.previous() }
        this.addMembers(viewer, gui, island)
        this.addExtraItems(gui, player)

        gui.open(player)
    }

    private fun addMembers(viewer: Member, gui: PaginatedGui, island: xyz.oribuin.skyblock.island.Island) {
        gui.clearPageItems()
        // Sort island members by role
        // Then sort each member by name in each role
        val roles = mutableListOf(Role.OWNER, Role.ADMIN, Role.MEMBER)
        val members = mutableListOf<Member>()

        roles.forEach { role ->
            island.members.filter { it.role == role }.forEach { member ->
                members.add(member)
            }
        }

        this.async {
            members.forEach { member ->
                gui.addItem(GuiItem(this.getPlayer(viewer, member)) {
                    if (viewer.role != Role.OWNER)
                        return@GuiItem

                    if (it.isLeftClick && member.role == Role.MEMBER)
                        member.role = Role.ADMIN

                    if (it.isRightClick && member.role == Role.ADMIN)
                        member.role = Role.MEMBER

                    this.addMembers(viewer, gui, island)
                })
            }

            gui.update()
        }


    }

    private fun getPlayer(viewer: Member, member: Member): ItemStack {

        val viewerPlayer = viewer.onlinePlayer ?: return ItemStack(Material.AIR)

        val lore = mutableListOf(
            " &f| &7Rank: #a6b2fc${xyz.oribuin.skyblock.hook.VaultHook.getRankCapital(viewerPlayer.world.name, member.offlinePlayer)}",
            " &f| &7Role: #a6b2fc${member.role.name.lowercase().replaceFirstChar { it.uppercase() }}",
            " &f| &7Balance: #a6b2fc${xyz.oribuin.skyblock.hook.VaultHook.getBalance(member.offlinePlayer)}",
            " &f| &7Last Login: #a6b2fc${if (member.offlinePlayer.isOnline) "Online" else dateFormat.format(member.offlinePlayer.lastLogin)}"
        )

        if (viewer.role == Role.OWNER && member.uuid != viewer.uuid) {
            lore.add(" &f| ")
            lore.add(" &f| #a6b2fcLeft-Click &7to promote.")
            lore.add(" &f| #a6b2fcRight-Click &7to demote.")
        }

        return skyblock.util.ItemBuilder(Material.PLAYER_HEAD)
            .name("#a6b2fc&l${member.offlinePlayer.name}".color())
            .owner(member.offlinePlayer)
            .lore(lore.map { it.color() })
            .build()
    }

    override val defaultValues: Map<String, Any>
        get() = mapOf(
            "#0" to "GUI Settings",
            "gui-settings.title" to "Island Members",
            "gui-settings.rows" to 4,

            "#1" to "Border Item",
            "border-item.enabled" to true,
            "border-item.material" to "BLACK_STAINED_GLASS_PANE",
            "border-item.name" to " ",
            "border-item.slots" to listOf("0-8", "27-35"),

            "#2" to "Next Page",
            "next-page.enabled" to true,
            "next-page.material" to "PAPER",
            "next-page.name" to "#a6b2fc&lNext Page",
            "next-page.slot" to 32,

            "#3" to "Previous Page",
            "previous-page.enabled" to true,
            "previous-page.material" to "PAPER",
            "previous-page.name" to "#a6b2fc&lPrevious Page",
            "previous-page.slot" to 30,

            "#4" to "Member Info",
            "member-info.enabled" to true,
            "member-info.material" to "PLAYER_HEAD",
            "member-info.name" to "#a6b2fc&lMember Info",
            "member-info.slot" to 31,
            "member-info.lore" to listOf(
                " &f| &7Here are all the members",
                " &f| &7of your island.",
            ),
            "member-info.owner" to "self"

        )

    override val menuName: String
        get() = "members-gui"
}