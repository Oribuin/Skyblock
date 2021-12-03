package xyz.oribuin.skyblock.gui

import org.apache.commons.lang.WordUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import xyz.oribuin.gui.Item
import xyz.oribuin.gui.PaginatedGui
import xyz.oribuin.orilibrary.util.HexUtils.colorify
import xyz.oribuin.orilibrary.util.StringPlaceholders
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.island.Warp
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.color
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.numRange
import xyz.oribuin.skyblock.util.send
import java.util.*

class WarpsGUI(private val plugin: SkyblockPlugin) {

    private val data = this.plugin.getManager<DataManager>()
    private val islandManager = this.plugin.getManager<IslandManager>()
    private var islands = this.data.islandCache.values.toMutableList()

    // Sorting & Filtering
    private var sortIterator = SortType.values().iterator().withIndex()
    private var sortType = sortIterator.next()

    private var filterIterator = FilterType.values().iterator().withIndex()
    private var filterType = filterIterator.next()

    // Cooldown because i know people are gonna spam through the options
    private val cooldown = mutableMapOf<UUID, Long>()

    fun create(member: Member) {
        val player = member.offlinePlayer.player ?: return

        val gui = PaginatedGui(45, "Island Warps", numRange(9, 35))
        // Stop people from yoinking items out the gui.
        gui.setDefaultClickFunction {
            it.isCancelled = true
            it.result = Event.Result.DENY
            (it.whoClicked as Player).updateInventory()
        }
        // Stop people from putting stuff in the gui.
        gui.setPersonalClickAction { gui.defaultClickFunction.accept(it) }

        gui.setItems(numRange(0, 8), Item.filler(Material.BLACK_STAINED_GLASS_PANE))
        gui.setItems(numRange(36, 44), Item.filler(Material.BLACK_STAINED_GLASS_PANE))

        this.sortGUI(sortType.value)
        this.setSortItems(gui, member)
        this.loadWarps(gui, member)
        gui.open(player)
    }

    private fun Enum<*>.format(): String = WordUtils.capitalizeFully(this.name.lowercase().replace("_", " "))

    private fun loadWarps(gui: PaginatedGui, member: Member) {
        gui.pageItems.clear()
        this.islands.filter { it.settings.public }.forEach {
            val warp = it.warp

            val placeholders = StringPlaceholders.builder("votes", warp.votes)
                .add("category", warp.category.name.lowercase().replaceFirstChar { x -> x.uppercase() })
                .add("visits", warp.visits)
                .add("owner", Bukkit.getOfflinePlayer(it.owner).name)

            val lore = mutableListOf(
                " &f| &7Owner: #a6b2fc%owner%",
                " &f| &7Category: #a6b2fc%category%",
                " &f| &7Visits: #a6b2fc%visits%",
                " &f| &7Votes: #a6b2fc%votes%",
                " &f|"
            )

            warp.desc.text.forEach { line -> lore.add(" &f| $line") }

            val item = Item.Builder(warp.icon)
                .setName(colorify("#a6b2fc&l${warp.name}"))
                .setLore(lore.map { s -> placeholders.apply(s).color() })
                .setOwner(Bukkit.getOfflinePlayer(it.owner))
                .create()

            gui.addPageItem(item) { event ->
//                if (!warp.public) {
//                    this.plugin.send(event.whoClicked, "warp-private")
//                    return@addPageItem
//                }

                // Check if the user is banned from the warp.
                val island = this.islandManager.islandFromID(warp.key) ?: return@addPageItem
                if (island.settings.banned.uuids.contains(event.whoClicked.uniqueId)) {
                    this.plugin.send(event.whoClicked, "is-banned")
                    return@addPageItem
                }

                this.islandManager.warpTeleport(warp, member)
            }
        }

        gui.update()
    }

    private fun setSortItems(gui: PaginatedGui, member: Member) {
        gui.setItem(
            37, Item.Builder(Material.OAK_SIGN)
                .setName("#a6b2fc&lSort Warps &7| &f&l${sortType.value.display}".color())
                .setLore(
                    " &f| &7Click to change the".color(),
                    " &f| &7way warps are sorted".color(),
                    " &f|".color(),
                    " &f| &7Next Sort: #a6b2fc${(SortType.values().getOrNull(sortType.index + 1) ?: SortType.NONE).display}".color()
                )
                .create()
        ) {
            if (it.whoClicked.uniqueId.onCooldown)
                return@setItem

            this.cooldown[it.whoClicked.uniqueId] = System.currentTimeMillis()

            if (!sortIterator.hasNext())
                sortIterator = SortType.values().iterator().withIndex()

            this.sortType = sortIterator.next()
            this.sortGUI(sortType.value)
            this.setSortItems(gui, member)
            this.loadWarps(gui, member)
        }

        gui.setItem(
            43, Item.Builder(Material.HOPPER)
                .setName("#a6b2fc&lFilter Warps &7| &f&l${filterType.value.format()}".color())
                .setLore(
                    " &f| &7Click to filter out".color(),
                    " &f| &7all the island warps".color(),
                    " &f|".color(),
                    " &f| &7Next Filter: #a6b2fc${(FilterType.values().getOrNull(filterType.index + 1) ?: FilterType.NONE).format()}".color()
                )
                .create()
        ) {

            if (it.whoClicked.uniqueId.onCooldown)
                return@setItem

            this.cooldown[it.whoClicked.uniqueId] = System.currentTimeMillis()

            if (!filterIterator.hasNext())
                filterIterator = FilterType.values().iterator().withIndex()

            this.filterType = filterIterator.next()

            this.sortGUI(sortType.value)
            this.filterGUI(filterType.value)
            this.setSortItems(gui, member)
            this.loadWarps(gui, member)
        }
    }

    /**
     * Sort a GUI by a specified type.
     *
     * @param type The way the gui will be sorted.
     */
    private fun sortGUI(type: SortType) {
        when (type) {
            // Warp Name
            SortType.NAMES_ASCENDING -> this.islands.sortBy { it.warp.name }
            SortType.NAMES_DESCENDING -> this.islands.sortByDescending { it.warp.name }
            // Warp Upvotes
            SortType.VOTES_ASCENDING -> this.islands.sortBy { it.warp.votes }
            SortType.VOTES_DESCENDING -> this.islands.sortByDescending { it.warp.votes }
            // Warp Visits
            SortType.VISITS_ASCENDING -> this.islands.sortBy { it.warp.visits }
            SortType.VISITS_DESCENDING -> this.islands.sortByDescending { it.warp.visits }

            // Other
            SortType.NONE -> {}
        }
    }

    private fun filterGUI(type: FilterType) {
        val list = this.data.islandCache.values.toMutableList()
        when (type) {
            FilterType.GENERAL -> list.removeIf { it.warp.category != Warp.Category.GENERAL }
            FilterType.FARMS -> list.removeIf { it.warp.category != Warp.Category.FARMS }
            FilterType.PARKOUR -> list.removeIf { it.warp.category != Warp.Category.PARKOUR }
            FilterType.SHOPS -> list.removeIf { it.warp.category != Warp.Category.SHOPS }
            FilterType.DESIGN -> list.removeIf { it.warp.category != Warp.Category.DESIGN }
            else -> {}
        }

        this.islands = list
    }

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
        VISITS_DESCENDING("Visits ↓"),
    }

    private enum class FilterType {
        NONE,

        // Categories
        GENERAL,
        FARMS,
        PARKOUR,
        SHOPS,
        DESIGN
    }

    private val UUID.onCooldown: Boolean
        get() = System.currentTimeMillis() <= (cooldown[this] ?: 0) + 1000

}