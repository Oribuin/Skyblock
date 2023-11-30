package xyz.oribuin.skyblock.enums

import xyz.oribuin.skyblock.island.Island

enum class FilterType {
    NONE,

    // Categories
    GENERAL,
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

        islands.removeIf { !it.warp.category.types.contains(this.name) }
        return islands
    }

}