package dev.oribuin.skyblock.gui.impl.warp;

public class WarpsGUI {

    //     private val manager = this.rosePlugin.getManager < skyblock.manager.IslandManager > ()
    //    private val menuManager = this.rosePlugin.getManager < skyblock.manager.MenuManager > ()
    //
    //    private val sortMap = mutableMapOf < UUID, dev.oribuin.skyblock.enums.SortType > ()
    //    private val filterMap = mutableMapOf < UUID, dev.oribuin.skyblock.enums.FilterType > ()
    //
    //    fun openMenu (member:Member){
    //        val player = member.onlinePlayer ?:return
    //
    //                val gui = this.createPagedGUI(player)
    //        this.put(gui, "info-item", player)
    //        this.put(gui, "border-item", player)
    //        this.put(gui, "back-item", player) {
    //            this.menuManager[dev.oribuin.skyblock.gui.PanelGUI:: class].openMenu(member)
    //        }
    //        this.put(gui, "next-page", player) {
    //            gui.next()
    //        }
    //        this.put(gui, "previous-page", player) {
    //            gui.previous()
    //        }
    //
    //        this.put(gui, "settings-item", player) {
    //            if (!member.hasIsland || member.role == Member.Role.MEMBER) {
    //                this.rosePlugin.send(player, "island-no-permission")
    //                return @put
    //            }
    //
    //            this.menuManager[dev.oribuin.skyblock.gui.WarpSettingsGUI:: class].openMenu(member)
    //        }
    //
    //        this.setDynamicItems(gui, member)
    //        this.loadWarps(gui, member)
    //        this.addExtraItems(gui, player)
    //
    //        gui.open(player)
    //    }
    //
    //    /**
    //     * Set all the dynamic sorting and filtering items.
    //     *
    //     * @param gui The GUI to set the items to.
    //     * @param member The member to get the settings from.
    //     */
    //    private fun setDynamicItems (gui:PaginatedGui, member:Member){
    //        val player = member.onlinePlayer ?:return
    //
    //                val sortType = this.sortMap[member.uuid] ?:dev.oribuin.skyblock.enums.SortType.VOTES_DESCENDING
    //        val filterType = this.filterMap[member.uuid] ?:dev.oribuin.skyblock.enums.FilterType.NONE
    //
    //        this.put(gui, "sort-item", player, StringPlaceholders.of("value", sortType.display)) {
    //            this.sortMap += member.uuid to skyblock.util.next(sortType)
    //            this.setDynamicItems(gui, member)
    //            this.loadWarps(gui, member)
    //        }
    //
    //        this.put(gui, "filter-item", player, StringPlaceholders.of("value", filterType.format())) {
    //            this.filterMap += member.uuid to skyblock.util.next(filterType)
    //            this.setDynamicItems(gui, member)
    //            this.loadWarps(gui, member)
    //        }
    //
    //    }
    //
    //    /**
    //     * Load all the warps into the GUI.
    //     *
    //     * @param gui The GUI to load the warps into.
    //     * @param member The member to get the warps from.
    //     */
    //    private fun loadWarps (gui:PaginatedGui, member:Member){
    //        gui.clearPageItems()
    //        var islands = manager.getIslands().toMutableList()
    //
    //        islands = (filterMap[member.uuid] ?:dev.oribuin.skyblock.enums.FilterType.NONE).filter(islands) // Filter the list
    //        islands = (sortMap[member.uuid] ?:dev.oribuin.skyblock.enums.SortType.VOTES_ASCENDING).sort(islands) // Sort the list
    //
    //        this.async {
    //            islands.filter {
    //                !it.warp.disabled && it.settings. public
    //            }.forEach {
    //                val warp = it.warp
    //                val placeholders = StringPlaceholders.builder("votes", warp.votes)
    //                        .add("category", warp.category.formatted())
    //                        .add("visits", warp.visits)
    //                        .add("owner", it.ownerMember.offlinePlayer.name)
    //                        .build()
    //
    //                var lore = listOf(
    //                        " <white>| <gray>Owner: #a6b2fc%owner%",
    //                        " <white>| <gray>Categories: #a6b2fc%category%",
    //                        " <white>| <gray>Visits: #a6b2fc%visits%",
    //                        " <white>| <gray>Votes: #a6b2fc%votes%",
    //                        )
    //
    //                lore = lore.map {
    //                    line -> placeholders.apply(line)
    //                }
    //
    //                val item = skyblock.util.ItemBuilder(warp.icon)
    //                        .name((<#a6b2fc><bold>" + warp.name).color())
    //                        .lore(lore.color())
    //                        .amount(1)
    //                        .build()
    //
    //                gui.addItem(GuiItem(item) {
    //                    _ -> this.manager.warpTeleport(it.warp, member)
    //                })
    //            }
    //
    //            gui.update()
    //        }
    //
    //    }
    //
    //    override val Map<String, Any>
    //    get() = mapOf(
    //            "#0"to"GUI Settings",
    //            "gui-settings.title"to"Island Warps",
    //            "gui-settings.rows"to 5,
    //
    //            "#1"to"Border Item",
    //            "border-item.enabled"to true,
    //            "border-item.material" to "BLACK_STAINED_GLASS_PANE",
    //            "border-item.name" to " ",
    //            "border-item.slots" to listOf ("0-8", "36-44"),
    //
    //    "#2" to "Back Item",
    //            "back-item.enabled" to true,
    //            "back-item.material" to "PLAYER_HEAD",
    //            "back-item.name" to <#a6b2fc><bold>Go Back",
    //            "back-item.lore" to listOf (
    //            " <white>| <gray>Click to go back",
    //            " <white>| <gray>to the main menu."
    //            ),
    //    "back-item.texture" to "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==",
    //            "back-item.slot" to 37,
    //
    //            "#3" to "Next Page Item",
    //            "next-page.enabled" to true,
    //            "next-page.material" to "PAPER",
    //            "next-page.name" to <#a6b2fc><bold>Next Page",
    //            "next-page.lore" to listOf (
    //            " <white>| <gray>Click to go to the",
    //            " <white>| <gray>next page."
    //            ),
    //    "next-page.slot" to 41,
    //
    //            "#4" to "Previous Page Item",
    //            "previous-page.enabled" to true,
    //            "previous-page.material" to "PAPER",
    //            "previous-page.name" to <#a6b2fc><bold>Previous Page",
    //            "previous-page.lore" to listOf (
    //            " <white>| <gray>Click to go to the",
    //            " <white>| <gray>previous page."
    //            ),
    //    "previous-page.slot" to 39,
    //
    //            "#5" to "Sort Item",
    //            "sort-item.enabled" to true,
    //            "sort-item.material" to "COMPARATOR",
    //            "sort-item.name" to <#a6b2fc><bold>Sort By: <gray>%value%",
    //            "sort-item.lore" to listOf (
    //            " <white>| <gray>Click to change the",
    //            " <white>| <gray>sorting method."
    //            ),
    //
    //    "sort-item.slot" to 3,
    //
    //            "#6" to "Filter Item",
    //            "filter-item.enabled" to true,
    //            "filter-item.material" to "HOPPER",
    //            "filter-item.name" to <#a6b2fc><bold>Filter By: <gray>%value%",
    //            "filter-item.lore" to listOf (
    //            " <white>| <gray>Click to change the",
    //            " <white>| <gray>filter method."
    //            ),
    //
    //    "filter-item.slot" to 5,
    //
    //            "#7" to "Info Item",
    //            "info-item.enabled" to true,
    //            "info-item.material" to "OAK_SIGN",
    //            "info-item.name" to <#a6b2fc><bold>Island Warps",
    //            "info-item.lore" to listOf (
    //            " <white>| <gray>Here are all the public",
    //            " <white>| <gray>warps on the server which will",
    //            " <white>| <gray>take you to any island."
    //            ),
    //    "info-item.slot" to 4,
    //
    //
    //            "#8" to "Warp Settings",
    //            "settings-item.enabled" to true,
    //            "settings-item.material" to "REDSTONE",
    //            "settings-item.name" to <#a6b2fc><bold>Warp Settings",
    //            "settings-item.lore" to listOf (
    //            " <white>| <gray>Click to change the",
    //            " <white>| <gray>island warp settings.",
    //            " <white>| ",
    //            " <white>| <gray>Requires #a6b2fcAdmin <gray>to view."
    //            ),
    //    "settings-item.slot" to 43,
    //        )
}
