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

class SettingsGUI(private val plugin: SkyblockPlugin, private val island: Island) {

    private val cooldown = mutableMapOf<UUID, Long>()

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

        gui.setItem(10, Item.Builder(Material.PLAYER_HEAD).setName("#a6b2fc&lGo Back".color()).setLore(" &f| &7Click to go back".color(), " &f| &7to the main page.".color()).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==").create()) {
            (it.whoClicked as Player).chat("/island")
        }

        this.setSettings(gui, member)

        gui.open(player)

    }

    private fun setSettings(gui: Gui, member: Member) {
        val settings = island.settings

        val nameLore = listOf(
            " &f| &7Click to change your",
            " &f| &7current island name.",
            " &f| ",
            " &f| &7Requires #a6b2fcAdmin &7role!"
        )

        gui.setItem(13, Item.Builder(Material.NAME_TAG).setName("#a6b2fc&lIsland Name".color()).setLore(nameLore.map { it.color() }).create()) {
            if (member.role == Member.Role.MEMBER) {
                this.plugin.send(it.whoClicked, "invalid-island-role")
                return@setItem
            }

            if (it.whoClicked.uniqueId.onCooldown())
                return@setItem

            this.editIslandName(gui, member)
        }

        val publicLore = listOf(
            " &f| &7Click to change public",
            " &f| &7access to your island.",
            " &f| ",
            " &f| &7Requires #a6b2fcAdmin &7role!"
        )

        gui.setItem(14, Item.Builder(Material.OAK_FENCE_GATE).setName("#a6b2fc&lPrivacy &f| ${if (settings.public) "#77dd77&lOpen" else "#ff6961&lClosed"}".color()).setLore(publicLore.map { it.color() }).create()) {
            if (member.role == Member.Role.MEMBER) {
                this.plugin.send(it.whoClicked, "invalid-island-role")
                return@setItem
            }

            if (it.whoClicked.uniqueId.onCooldown())
                return@setItem

            settings.public = !settings.public
            data.islandCache[island.key] = island
            this.cooldown[it.whoClicked.uniqueId] = System.currentTimeMillis()

            val placeholders = StringPlaceholders.builder("setting", "Privacy")
                .add("player", it.whoClicked.name)
                .add("value", if (settings.public) "Open" else "Closed")
                .build()

            this.setSettings(gui, member)
            this.sendSettingMessage(placeholders)
        }

        val animalLore = listOf(
            " &f| &7Click to turn animal spawning",
            " &f| &7on/off on your island.",
            " &f| ",
            " &f| &7Requires #a6b2fcAdmin &7role!"
        )

        gui.setItem(15, Item.Builder(Material.WHEAT).setName("#a6b2fc&lAnimals &f| ${if (settings.animalSpawning) "#77dd77&lOn" else "#ff6961&lOff"}".color()).setLore(animalLore.map { it.color() }).create()) {
            if (member.role == Member.Role.MEMBER) {
                this.plugin.send(it.whoClicked, "invalid-island-role")
                return@setItem
            }

            if (it.whoClicked.uniqueId.onCooldown())
                return@setItem

            settings.animalSpawning = !settings.animalSpawning
            data.islandCache[island.key] = island
            this.cooldown[it.whoClicked.uniqueId] = System.currentTimeMillis()

            val placeholders = StringPlaceholders.builder("setting", "Animal Spawning")
                .add("player", it.whoClicked.name)
                .add("value", if (settings.animalSpawning) "On" else "Off")
                .build()

            this.setSettings(gui, member)
            this.sendSettingMessage(placeholders)
        }

        val mobLore = listOf(
            " &f| &7Click to turn mob spawning",
            " &f| &7on/off on your island.",
            " &f| ",
            " &f| &7Requires #a6b2fcAdmin &7role!"
        )

        gui.setItem(16, Item.Builder(Material.BONE).setName("#a6b2fc&lMobs &f| ${if (settings.mobSpawning) "#77dd77&lOn" else "#ff6961&lOff"}".color()).setLore(mobLore.map { it.color() }).create()) {
            if (member.role == Member.Role.MEMBER) {
                this.plugin.send(it.whoClicked, "invalid-island-role")
                return@setItem
            }

            if (it.whoClicked.uniqueId.onCooldown())
                return@setItem

            settings.mobSpawning = !settings.mobSpawning
            data.islandCache[island.key] = island
            this.cooldown[it.whoClicked.uniqueId] = System.currentTimeMillis()

            val placeholders = StringPlaceholders.builder("setting", "Mob Spawning")
                .add("player", it.whoClicked.name)
                .add("value", if (settings.mobSpawning) "On" else "Off")
                .build()

            this.setSettings(gui, member)
            this.sendSettingMessage(placeholders)
        }
        gui.update()
    }

    /**
     * Edit the island's name using an anvil gui
     *
     * @param gui The GUI to go back to
     * @param member The island member changing the name.
     */
    private fun editIslandName(gui: Gui, member: Member) {

        val player = member.offlinePlayer.player ?: return
        AnvilGUI.Builder()
            .plugin(this.plugin)
            .title("Island Name")
            .title(island.settings.name)
            .itemLeft(Item.filler(Material.NAME_TAG).item)
            .onClose { gui.open(it.player) }
            .onComplete { user, text ->

                if (text.equals(island.settings.name, ignoreCase = true))
                    return@onComplete AnvilGUI.Response.close()

                this.cooldown[user.uniqueId] = System.currentTimeMillis()
                island.settings.name = text
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
            .forEach { this.plugin.send(it, "changed-settings", placeholders) }
    }

    private fun UUID.onCooldown(): Boolean = System.currentTimeMillis() <= (cooldown[this] ?: 0) + 3000


}