package xyz.oribuin.skyblock.enums

import xyz.oribuin.skyblock.island.Island

enum class SortType(val display: String) {
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
            NAMES_ASCENDING -> islands.sortBy { it.warp.name }
            NAMES_DESCENDING -> islands.sortByDescending { it.warp.name }

            // Warp Votes
            VOTES_ASCENDING -> islands.sortBy { it.warp.votes }
            VOTES_DESCENDING -> islands.sortByDescending { it.warp.votes }

            // Warp Visits
            VISITS_ASCENDING -> islands.sortBy { it.warp.visits }
            VISITS_DESCENDING -> islands.sortByDescending { it.warp.visits }
            else -> {}
        }

        return islands
    }

}
