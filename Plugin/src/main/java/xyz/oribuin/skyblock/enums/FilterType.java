package xyz.oribuin.skyblock.enums;

import xyz.oribuin.skyblock.island.Island;

import java.util.List;

public enum FilterType {
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
    public List<Island> filter(List<Island> islands) {
        if (this == FilterType.NONE)
            return islands;

        islands.removeIf(island -> !island.getWarp().getCategory().getTypes().contains(this.name()));
        return islands;
    }

}