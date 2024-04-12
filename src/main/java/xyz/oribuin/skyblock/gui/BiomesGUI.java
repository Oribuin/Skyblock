package xyz.oribuin.skyblock.gui;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

class BiomesGUI(rosePlugin:RosePlugin) :xyz.oribuin.skyblock.gui.

PluginGUI(rosePlugin) {

    private val islandManager = this.rosePlugin.getManager < skyblock.manager.IslandManager > ()

    fun openMenu (member:Member){
        val player = member.onlinePlayer ?:return
                val island = member.getIsland(this.rosePlugin) ?:return
                val gui = this.createPagedGUI(player)

        this.put(gui, "border-item", player)
        this.put(gui, "next-page", player) {
            gui.next()
        }
        this.put(gui, "go-back", player) {
            this.rosePlugin.getManager < skyblock.manager.MenuManager > ()[xyz.oribuin.skyblock.gui.PanelGUI:: class].openMenu(member)
        }
        this.put(gui, "previous-page", player) {
            gui.previous()
        }
        this.addExtraItems(gui, player)

        this.islandManager.biomeMap.forEach {
            (_, islandBiome) ->
                    val biomeName = islandBiome.biome.name.lowercase().replace("_", " ").replaceFirstChar {
                it.uppercase()
            }

            val newBiomeItem = skyblock.util.ItemBuilder(islandBiome.icon)
                    .name("#a6b2fc&l$biomeName".color())
                    .lore(
                            listOf(
                                    " &f| #a6b2fcShift-Left Click&7 to change",
                                    " &f| &7your island to this biome.",
                                    " &f|",
                                    " &f| &7Cost: #a6b2fc$${String.format(" % .2f", islandBiome.cost)}"
                            ).color()
                    )
                    .build()

            val guiItem = GuiItem(newBiomeItem)
            guiItem.setAction {

                if (it.click != ClickType.SHIFT_LEFT) {
                    return @setAction
                }

                val whoClicked = it.whoClicked as Player
                if (island.settings.biome == islandBiome.biome) {
                    return @setAction
                }

                if (!xyz.oribuin.skyblock.hook.VaultHook.has(whoClicked, islandBiome.cost)) {
                    this.rosePlugin.send(whoClicked, "no-money")
                    return @setAction
                }


                whoClicked.closeInventory()
                if (xyz.oribuin.skyblock.hook.VaultHook.withdraw(whoClicked, islandBiome.cost)) {

                    island.settings.biome = islandBiome.biome
                    islandManager.setIslandBiome(island)
                    island.cache(this.rosePlugin)

                    island.members.mapNotNull {
                        member -> member.onlinePlayer
                    }.forEach {
                        member ->
                                this.rosePlugin.send(
                                        member,
                                        "command-biome-success",
                                        StringPlaceholders.of("biome", biomeName)
                                )
                    }
                }


            }

            gui.addItem(guiItem)
        }

        gui.open(player)
    }

    override val Map<String, Any>
    get() = mapOf(
            "#0"to"GUI Settings",
            "gui-settings.title"to"Island Biomes",
            "gui-settings.rows"to 4,

            "#1"to"Previous Page",
            "previous-page.name"to"#a6b2fc&lPrevious Page",
            "previous-page.lore"to listOf(" &f| #a6b2fcLeft Click&7 to go to the previous page."),
            "previous-page.material"to Material.PAPER.toString(),
            "previous-page.glow"to true,
            "previous-page.slot" to 29,

            "#2" to "Go Back",
            "go-back.enable" to true,
            "go-back.name" to "#a6b2fc&lGo Back",
            "go-back.lore" to listOf (" &f| &7Click to go back to", " &f| &7the main island menu."),
    "go-back.material" to Material.PLAYER_HEAD.toString(),
            "go-back.slot" to 31,
            "go-back.texture" to "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ",

            "#3" to "Next Page",
            "next-page.name" to "#a6b2fc&lNext Page",
            "next-page.lore" to listOf (" &f| #a6b2fcLeft Click&7 to go to the next page."),
            "next-page.material" to Material.PAPER.toString(),
            "next-page.glow" to true,
            "next-page.slot" to 33,

            "#4" to "Border Item",
            "border-item.enabled" to true,
            "border-item.name" to "",
            "border-item.material" to Material.BLACK_STAINED_GLASS_PANE.toString(),
            "border-item.slots" to listOf ("0-8", "27-35"),
        )


    override val String
    get() = "biomes-gui"

}