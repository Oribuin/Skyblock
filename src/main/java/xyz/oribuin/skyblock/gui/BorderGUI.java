package xyz.oribuin.skyblock.gui;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Material;
import xyz.oribuin.skyblock.nms.BorderColor;
import xyz.oribuin.skyblock.util.deserialize;
import xyz.oribuin.skyblock.util.format;
import xyz.oribuin.skyblock.util.getManager;
import xyz.oribuin.skyblock.util.send;
import java.util.*;

class BorderGUI(rosePlugin: RosePlugin) : xyz.oribuin.skyblock.gui.PluginGUI(rosePlugin) {

    private val data = this.rosePlugin.getManager<skyblock.manager.DataManager>()
    private val activeColors = mutableMapOf<UUID, BorderColor>()

    fun openMenu(player: Player) {
        val gui = this.createGUI(player)
        val member = this.data.getMember(player.uniqueId)
        if (!this.activeColors.containsKey(player.uniqueId)) {
            this.activeColors[player.uniqueId] = member.border
        }

        gui.setCloseGuiAction {
            val active = this.activeColors[it.player.uniqueId] ?: return@setCloseGuiAction

            if (active != member.border) {
                member.border = active
                with(data) { member.save() }

                this.rosePlugin.send(
                    it.player,
                    "command-border-success",
                    StringPlaceholders.of("border", skyblock.util.format())
                )
            }


            val islandManager = this.rosePlugin.getManager<skyblock.manager.IslandManager>()
            val island = islandManager.getIslandFromLoc(it.player.location) ?: return@setCloseGuiAction
            islandManager.createBorder(member, island)
        }

        this.put(gui, "border-item", player)
        this.put(gui, "border-info", player)
        this.addExtraItems(gui, player)

        gui.open(player)
        gui.setBorderIcons(player)
    }

    private fun Gui.setBorderIcons(player: Player) {

        val currentActive = activeColors[player.uniqueId] ?: BorderColor.BLUE

        for (color in BorderColor.entries) {
            val itemStack = skyblock.util.ItemBuilder(skyblock.util.deserialize(config, player, "${color.name.lowercase()}-border") ?: return)
                .glow(color == currentActive)
                .build()

            val slot = config.getInt("${color.name.lowercase()}-border.slot")
            this.setItem(slot, GuiItem(itemStack) {
                activeColors[player.uniqueId] = color
                this.setBorderIcons(player)
            })
        }

        this.update()
        this.updateTitle(
            formatString(
                player,
                get("gui-settings.title", "Border Color %color%"),
                StringPlaceholders.of("color", currentActive.format())
            )
        )

    }

    private val borderLore: List<String>
        get() = mutableListOf(
            "&f | &7Click to change your",
            "&f | &7personal island border",
            "&f | &7to this color!"
        )

    override val defaultValues: Map<String, Any>
        get() = mapOf(
            "#0" to "GUI Settings",
            "gui-settings.title" to "Border Color | %color%",
            "gui-settings.rows" to 3,

            "#1" to "Red Border",
            "red-border.material" to Material.RED_DYE.name,
            "red-border.name" to "#ff6961&lRed Border",
            "red-border.lore" to borderLore,
            "red-border.slot" to 12,

            "#2" to "Green Border",
            "green-border.material" to Material.LIME_DYE.name,
            "green-border.name" to "#77dd77&lGreen Border",
            "green-border.lore" to borderLore,
            "green-border.slot" to 13,

            "#3" to "Blue Border",
            "blue-border.material" to Material.LIGHT_BLUE_DYE.name,
            "blue-border.name" to "#417cfc&lBlue Border",
            "blue-border.lore" to borderLore,
            "blue-border.slot" to 14,

            "#4" to "Invisible Border",
            "off-border.material" to Material.GRAY_DYE.name,
            "off-border.name" to "#a6b2fc&lInvisible Border",
            "off-border.lore" to borderLore,
            "off-border.slot" to 15,

            "#5" to "Border Info",
            "border-info.enabled" to true,
            "border-info.material" to Material.SPRUCE_SIGN.name,
            "border-info.name" to "#a6b2fc&lBorder Color",
            "border-info.lore" to listOf(
                "&f | &7Click on the dyes change",
                "&f | &7your personal border color!",
                "",
                "&f | &7This is only visible to you."
            ),
            "border-info.slot" to 10,

            "#6" to "Border Item",
            "border-item.enabled" to true,
            "border-item.material" to Material.BLACK_STAINED_GLASS_PANE.name,
            "border-item.name" to " ",
            "border-item.slots" to listOf("0-26"),
        )

    override val menuName: String
        get() = "border-gui"
}