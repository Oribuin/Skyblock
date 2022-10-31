package xyz.oribuin.skyblock.gui

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.utils.StringPlaceholders
import dev.triumphteam.gui.guis.GuiItem
import dev.triumphteam.gui.guis.PaginatedGui
import java.util.*
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.manager.MenuManager
import xyz.oribuin.skyblock.util.EnumIterator
import xyz.oribuin.skyblock.util.ItemBuilder
import xyz.oribuin.skyblock.util.color
import xyz.oribuin.skyblock.util.format
import xyz.oribuin.skyblock.util.formatEnum
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.send


class WarpsGUI(rosePlugin: RosePlugin) : PluginGUI(rosePlugin) {

    private val manager = this.rosePlugin.getManager<IslandManager>()
    private val menuManager = this.rosePlugin.getManager<MenuManager>()
    private val sortMap = mutableMapOf<UUID, SortType>()
    private val filterMap = mutableMapOf<UUID, FilterType>()

    fun openMenu(member: Member) {
        val player = member.onlinePlayer ?: return

        val gui = this.createPagedGUI(player)
        this.put(gui, "info-item", player)
        this.put(gui, "border-item", player)
        this.put(gui, "back-item", player) { this.menuManager[PanelGUI::class].openMenu(member) }
        this.put(gui, "next-page", player) { gui.next() }
        this.put(gui, "previous-page", player) { gui.previous() }

        this.put(gui, "settings-item", player) {
            if (!member.hasIsland || member.role == Member.Role.MEMBER) {
                this.rosePlugin.send(player, "island-no-permission")
                return@put
            }

            this.menuManager[WarpSettingsGUI::class].openMenu(member)
        }

        this.setDynamicItems(gui, member)
        this.loadWarps(gui, member)

        gui.open(player)
    }

    /**
     * Set all the dynamic sorting and filtering items.
     *
     * @param gui The GUI to set the items to.
     * @param member The member to get the settings from.
     */
    private fun setDynamicItems(gui: PaginatedGui, member: Member) {
        val player = member.onlinePlayer ?: return

        val sortType = sortMap[member.uuid] ?: SortType.NONE
        val filterType = filterMap[member.uuid] ?: FilterType.NONE

        val sortIterator = EnumIterator(SortType::class)
        sortIterator.skipTo(sortType)

        this.put(gui, "sort-item", player, StringPlaceholders.single("value", sortType.display)) {
            sortIterator.next()

            this.sortMap[member.uuid] = sortIterator.get()
            this.setDynamicItems(gui, member)
            this.loadWarps(gui, member)
        }

        val filterIterator = EnumIterator(FilterType::class)
        filterIterator.skipTo(filterType)

        this.put(gui, "filter-item", player, StringPlaceholders.single("value", filterType.name.formatEnum())) {
            filterIterator.next()

            this.filterMap[member.uuid] = filterIterator.get()
            this.setDynamicItems(gui, member)
            this.loadWarps(gui, member)
        }

    }

    /**
     * Load all the warps into the GUI.
     *
     * @param gui The GUI to load the warps into.
     * @param member The member to get the warps from.
     */
    private fun loadWarps(gui: PaginatedGui, member: Member) {
        gui.clearPageItems()
        val islands = manager.getIslands().toMutableList()

        (filterMap[member.uuid] ?: FilterType.NONE).filter(islands) // Filter the list
        (sortMap[member.uuid] ?: SortType.NONE).sort(islands) // Sort the list

        this.async {
            islands.filter { !it.warp.disabled && it.settings.public }.forEach {
                val warp = it.warp
                val placeholders = StringPlaceholders.builder("votes", warp.votes)
                    .addPlaceholder("category", warp.category.types.map { type -> type.name.formatEnum() }.format())
                    .addPlaceholder("visits", warp.visits)
                    .addPlaceholder("owner", it.ownerMember.offlinePlayer.name)
                    .build()

                var lore = listOf(
                    " &f| &7Owner: #a6b2fc%owner%",
                    " &f| &7Categories: #a6b2fc%category%",
                    " &f| &7Visits: #a6b2fc%visits%",
                    " &f| &7Votes: #a6b2fc%votes%",
                )

                lore = lore.map { line -> placeholders.apply(line) }

                val item = ItemBuilder(warp.icon)
                    .name(("#a6b2fc&l" + warp.name).color())
                    .lore(lore.color())
                    .build()

                gui.addItem(GuiItem(item) { _ -> this.manager.warpTeleport(it.warp, member) })
            }

            gui.update()
        }

    }

    override val defaultValues: Map<String, Any>
        get() = mapOf(
            "#0" to "GUI Settings",
            "gui-settings.title" to "Island Warps",
            "gui-settings.rows" to 5,

            "#1" to "Border Item",
            "border-item.enabled" to true,
            "border-item.material" to "BLACK_STAINED_GLASS_PANE",
            "border-item.name" to " ",
            "border-item.slots" to listOf("0-8", "36-44"),

            "#2" to "Back Item",
            "back-item.enabled" to true,
            "back-item.material" to "PLAYER_HEAD",
            "back-item.name" to "#a6b2fc&lGo Back",
            "back-item.lore" to listOf(
                " &f| &7Click to go back",
                " &f| &7to the main menu."
            ),
            "back-item.texture" to "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==",
            "back-item.slot" to 37,

            "#3" to "Next Page Item",
            "next-page.enabled" to true,
            "next-page.material" to "PAPER",
            "next-page.name" to "#a6b2fc&lNext Page",
            "next-page.lore" to listOf(
                " &f| &7Click to go to the",
                " &f| &7next page."
            ),
            "next-page.slot" to 41,

            "#4" to "Previous Page Item",
            "previous-page.enabled" to true,
            "previous-page.material" to "PAPER",
            "previous-page.name" to "#a6b2fc&lPrevious Page",
            "previous-page.lore" to listOf(
                " &f| &7Click to go to the",
                " &f| &7previous page."
            ),
            "previous-page.slot" to 39,

            "#5" to "Sort Item",
            "sort-item.enabled" to true,
            "sort-item.material" to "COMPARATOR",
            "sort-item.name" to "#a6b2fc&lSort By: &7%value%",
            "sort-item.lore" to listOf(
                " &f| &7Click to change the",
                " &f| &7sorting method."
            ),

            "sort-item.slot" to 3,

            "#6" to "Filter Item",
            "filter-item.enabled" to true,
            "filter-item.material" to "HOPPER",
            "filter-item.name" to "#a6b2fc&lFilter By: &7%value%",
            "filter-item.lore" to listOf(
                " &f| &7Click to change the",
                " &f| &7filter method."
            ),

            "filter-item.slot" to 5,

            "#7" to "Info Item",
            "info-item.enabled" to true,
            "info-item.material" to "OAK_SIGN",
            "info-item.name" to "#a6b2fc&lIsland Warps",
            "info-item.lore" to listOf(
                " &f| &7Here are all the public",
                " &f| &7warps on the server which will",
                " &f| &7take you to any island."
            ),
            "info-item.slot" to 4,


            "#8" to "Warp Settings",
            "settings-item.enabled" to true,
            "settings-item.material" to "REDSTONE",
            "settings-item.name" to "#a6b2fc&lWarp Settings",
            "settings-item.lore" to listOf(
                " &f| &7Click to change the",
                " &f| &7island warp settings.",
                " &f| ",
                " &f| &7Requires #a6b2fcAdmin &7to view."
            ),
            "settings-item.slot" to 43,
        )

    override val menuName: String
        get() = "warp-gui"

    private enum class SortType(val display: String) {
        NONE("None"),

        // Sort by warp names.
        NAMES_ASCENDING("Names ↑"),
        NAMES_DESCENDING("Names ↓"),

        // Sort by island up votes
        VOTES_ASCENDING("Votes ↑"),
        VOTES_DESCENDING("Votes ↓"),

        // Sort by island visits
        VISITS_ASCENDING("Visits ↑"),
        VISITS_DESCENDING("Visits ↓");


        /**
         * Sort the list of islands by the given type.
         *
         * @param islands The list of islands to sort.
         * @return The sorted list of islands.
         */
        fun sort(islands: MutableList<Island>): MutableList<Island> {
            when (this) {
                // Warp Names
                NAMES_ASCENDING -> islands.sortedBy { it.warp.name }
                NAMES_DESCENDING -> islands.sortedByDescending { it.warp.name }

                // Warp Votes
                VOTES_ASCENDING -> islands.sortedBy { it.warp.votes }
                VOTES_DESCENDING -> islands.sortedByDescending { it.warp.votes }

                // Warp Visits
                VISITS_ASCENDING -> islands.sortedBy { it.warp.visits }
                VISITS_DESCENDING -> islands.sortedByDescending { it.warp.visits }
                else -> {}
            }

            return islands
        }

    }

    enum class FilterType {
        NONE,

        // Categories
        CATEGORY,
        FARMS,
        PARKOUR,
        SHOPS,
        DESIGN;

        /**
         * Filter the list of islands by the given type.
         *
         * @param islands The list of islands to filter.
         * @return The filtered list of islands.
         */
        fun filter(islands: MutableList<Island>): MutableList<Island> {
            if (this == NONE)
                return islands

            islands.removeIf { !it.warp.category.names.contains(this.name) }
            return islands
        }

    }

}