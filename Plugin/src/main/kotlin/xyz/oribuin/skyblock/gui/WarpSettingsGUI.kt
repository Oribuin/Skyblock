package xyz.oribuin.skyblock.gui

import net.wesjd.anvilgui.AnvilGUI
import org.apache.commons.lang.WordUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import xyz.oribuin.gui.Gui
import xyz.oribuin.gui.Item
import xyz.oribuin.gui.PaginatedGui
import xyz.oribuin.orilibrary.util.StringPlaceholders
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.*
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
            (it.whoClicked as Player).chat("/island warp")
        }

        this.setSettings(gui, member)

        gui.open(player)

    }

    private fun setSettings(gui: Gui, member: Member) {
        val nameLore = listOf(
            " &f| &7Click to change your",
            " &f| &7current island warp name.",
            " &f| ",
            " &f| &7Requires #a6b2fcAdmin &7role!"
        )

        gui.setItem(12, Item.Builder(Material.ANVIL).setName("#a6b2fc&lWarp Name".color()).setLore(nameLore.color()).create()) {
            if (member.role == Member.Role.MEMBER) {
                this.plugin.send(it.whoClicked, "invalid-island-role")
                return@setItem
            }

            if (it.whoClicked.uniqueId.onCooldown)
                return@setItem

            this.editWarpName(gui, member)
        }

        val descriptionLore = listOf(
            " &f| &7Click to change your",
            " &f| &7current island warp description.",
            " &f| ",
            " &f| &7Requires #a6b2fcAdmin &7role!"
        )

        gui.setItem(13, Item.Builder(Material.OAK_SIGN).setName("#a6b2fc&lWarp Description".color()).setLore(descriptionLore.color()).create()) {
            if (member.role == Member.Role.MEMBER) {
                this.plugin.send(it.whoClicked, "invalid-island-role")
                return@setItem
            }

            WarpDescGUI(this.plugin, this.island, it.whoClicked as Player)
        }

        val iconLore = listOf(
            " &f| &7Click to change your",
            " &f| &7current island warp icon.",
            " &f| ",
            " &f| &7Requires #a6b2fcAdmin &7role!"
        )

        gui.setItem(14, Item.Builder(Material.ITEM_FRAME).setName("#a6b2fc&lWarp Icon".color()).setLore(iconLore.color()).create()) {
            if (member.role == Member.Role.MEMBER) {
                this.plugin.send(it.whoClicked, "invalid-island-role")
                return@setItem
            }

            if (it.whoClicked.uniqueId.onCooldown)
                return@setItem

            this.editWarpIcon(member)
        }

        val categoryLore = listOf(
            " &f| &7Click to change your",
            " &f| &7current island warp category.",
            " &f| ",
            " &f| &7Requires #a6b2fcAdmin &7role!"
        )

        gui.setItem(15, Item.Builder(Material.NAME_TAG).setName("#a6b2fc&lWarp Category".color()).setLore(categoryLore.color()).create()) {
            if (member.role == Member.Role.MEMBER) {
                this.plugin.send(it.whoClicked, "invalid-island-role")
                return@setItem
            }

            WarpCategoryGUI(this.plugin, this.island).create(member)
        }

        val homeLore = listOf(
            " &f| &7Click to change your",
            " &f| &7current island warp home" +
                    ".",
            " &f| ",
            " &f| &7Requires #a6b2fcAdmin &7role!"
        )

        gui.setItem(16, Item.Builder(Material.BLUE_BED).setName("#a6b2fc&lWarp Location".color()).setLore(homeLore.color()).create()) {
            if (member.role == Member.Role.MEMBER) {
                this.plugin.send(it.whoClicked, "invalid-island-role")
                return@setItem
            }

            val placeholders = StringPlaceholders.builder("setting", "Location")
                .add("player", it.whoClicked.name)
                .add("value", it.whoClicked.location.center().format())
                .build()

            this.sendSettingMessage(placeholders)
            island.warp.location = it.whoClicked.location.center()
            data.saveIsland(island)
            it.whoClicked.closeInventory()
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
            .text(island.warp.name)
            .title(island.warp.name)
            .itemLeft(Item.filler(Material.NAME_TAG).item)
            .onComplete { user, text ->

                if (text.equals(island.warp.name, ignoreCase = true))
                    return@onComplete AnvilGUI.Response.close()

                this.cooldown[user.uniqueId] = System.currentTimeMillis()
                island.warp.name = text
                this.data.islandCache[island.key] = island

                val placeholders = StringPlaceholders.builder("setting", "Name")
                    .add("player", user.name)
                    .add("value", text)
                    .build()

                this.sendSettingMessage(placeholders)
                this.setSettings(gui, member)
                return@onComplete AnvilGUI.Response.close()
            }
            .open(player)
    }

    private fun editWarpIcon(member: Member) {
        val iconGUI = PaginatedGui(45, "Warp Icon", numRange(9, 35))
        iconGUI.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }

        iconGUI.setPersonalClickAction { iconGUI.defaultClickFunction.accept(it) }
        iconGUI.setItems(numRange(0, 8), Item.filler(Material.BLACK_STAINED_GLASS_PANE))
        iconGUI.setItems(numRange(36, 44), Item.filler(Material.BLACK_STAINED_GLASS_PANE))

        iconGUI.setItem(40, Item.Builder(Material.PLAYER_HEAD).setName("#a6b2fc&lGo Back".color()).setLore(" &f| &7Click to go back".color(), " &f| &7to the main page.".color()).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==").create()) {
            (it.whoClicked as Player).chat("/island warp settings")
        }

        iconGUI.setItem(38, Item.Builder(Material.PAPER).setName("#a6b2fc&lPrevious Page".color()).create()) { iconGUI.previous(it.whoClicked as Player) }
        iconGUI.setItem(42, Item.Builder(Material.PAPER).setName("#a6b2fc&lNext Page".color()).create()) { iconGUI.next(it.whoClicked as Player) }

        Material.values().toMutableList().filter { it.isItem && !it.isAir }.sortedBy { it.name }.forEach {
            val formattedName = WordUtils.capitalizeFully(it.name.lowercase().replace("_", " "))
            val item = Item.Builder(it)
                .setName("#a6b2fc&l$formattedName".color())
                .create()

            iconGUI.addPageItem(item) { event ->
                island.warp.icon = it;
                data.saveIsland(island)
                event.whoClicked.closeInventory()

                val placeholders = StringPlaceholders.builder("setting", "Icon")
                    .add("player", member.offlinePlayer.name)
                    .add("value", formattedName)
                    .build()


                this.sendSettingMessage(placeholders)
            }
        }

        val player = member.offlinePlayer.player ?: return
        iconGUI.open(player)
    }


    private fun sendSettingMessage(placeholders: StringPlaceholders) {
        island.members.mapNotNull { it.offlinePlayer.player }
            .forEach { this.plugin.send(it, "changed-warp", placeholders) }
    }

    private val UUID.onCooldown: Boolean
        get() = System.currentTimeMillis() <= (cooldown[this] ?: 0) + 500


}