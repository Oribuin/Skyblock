package xyz.oribuin.skyblock.gui

import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import xyz.oribuin.gui.Gui
import xyz.oribuin.gui.Item
import xyz.oribuin.orilibrary.util.StringPlaceholders
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.color
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.numRange
import xyz.oribuin.skyblock.util.send
import java.util.*

class WarpSettingsGUI(private val plugin: SkyblockPlugin, private val island: Island) {

    private val cooldown = mutableMapOf<UUID, Long>()

    private val data = this.plugin.getManager<DataManager>()
    private val islandManager = this.plugin.getManager<IslandManager>()

    fun create(member: Member) {

        val player = member.offlinePlayer.player ?: return

        val gui = Gui(27, "Warp Settings")
        // Stop people from yoinking items out the gui.
        gui.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }
        // Stop people from putting stuff in the gui.
        gui.setPersonalClickAction { gui.defaultClickFunction.accept(it) }
        gui.setItems(numRange(0, 26), Item.filler(Material.BLACK_STAINED_GLASS_PANE))

        gui.setItem(10, Item.Builder(Material.PLAYER_HEAD).setName("#a6b2fc&lGo Back".color()).setLore(" &f| &7Click to go back".color(), " &f| &7to the main page.".color()).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==").create()) {
            (it.whoClicked as Player).chat("/island")
        }

        this.setSettings(gui, member)

        gui.open(player)

    }

    private fun setSettings(gui: Gui, member: Member) {
        val warp = island.warp

        val nameLore = listOf(
            " &f| &7Click to change your",
            " &f| &7current island warp name.",
            " &f| ",
            " &f| &7Requires #a6b2fcAdmin &7role!"
        )

        gui.setItem(13, Item.Builder(Material.NAME_TAG).setName("#a6b2fc&lWarp Name".color()).setLore(nameLore.map { it.color() }).create()) {
            if (member.role == Member.Role.MEMBER) {
                this.plugin.send(it.whoClicked, "invalid-island-role")
                return@setItem
            }

            if (it.whoClicked.uniqueId.onCooldown())
                return@setItem

            this.editWarpName(gui, member)
        }

        gui.update()
    }

    /**
     * Edit the island's name using an anvil gui
     *
     * @param gui The GUI to go back to
     * @param member The island member changing the name.
     */
    private fun editWarpName(gui: Gui, member: Member) {

        val player = member.offlinePlayer.player ?: return
        AnvilGUI.Builder()
            .plugin(this.plugin)
            .title("Warp Name")
            .title(island.warp.name)
            .itemLeft(Item.filler(Material.NAME_TAG).item)
            .onComplete { user, text ->

                if (text.equals(island.settings.name, ignoreCase = true))
                    return@onComplete AnvilGUI.Response.close()

                this.cooldown[user.uniqueId] = System.currentTimeMillis()
                island.warp.name = text
                this.data.islandCache[island.key] = island

                val placeholders = StringPlaceholders.builder("setting", "Name")
                    .add("player", user.name)
                    .add("value", text)
                    .build()


                this.setSettings(gui, member)
                this.sendSettingMessage(placeholders)
                return@onComplete AnvilGUI.Response.close()
            }
            .open(player)
    }

    private fun sendSettingMessage(placeholders: StringPlaceholders) {
        island.members.filter { it.offlinePlayer.player != null }
            .map { it.offlinePlayer.player!! }
            .forEach { this.plugin.send(it, "changed-warp", placeholders) }
    }

    private fun UUID.onCooldown(): Boolean = System.currentTimeMillis() <= (cooldown[this] ?: 0) + 3000


}