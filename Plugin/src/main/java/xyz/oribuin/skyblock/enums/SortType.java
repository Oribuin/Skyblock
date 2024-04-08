package xyz.oribuin.skyblock.enums;

import xyz.oribuin.skyblock.island.Island;

import java.util.List;

public enum  SortType() {
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

    private String display;

    SortType(String display) {
        this.display = display;
    }

    /**
     * Sort the list of islands by the given type.
     *
     * @param islands The list of islands to sort.
     * @return The sorted list of islands.
     */
    public List<Island> sort(List<Island> islands) {
        if (this == SortType.NONE)
            return islands;

        islands.sort((a, b) -> switch (this) {
            case NAMES_ASCENDING -> a.getWarp().getName().compareTo(b.getWarp().getName());
            case NAMES_DESCENDING -> b.getWarp().getName().compareTo(a.getWarp().getName());
            case VOTES_ASCENDING -> Integer.compare(a.getWarp().getVotes(), b.getWarp().getVotes());
            case VOTES_DESCENDING -> Integer.compare(b.getWarp().getVotes(), a.getWarp().getVotes());
            case VISITS_ASCENDING -> Integer.compare(a.getWarp().getVisits(), b.getWarp().getVisits());
            case VISITS_DESCENDING -> Integer.compare(b.getWarp().getVisits(), a.getWarp().getVisits());
            default -> 0;
        });

        return islands;
    }


}
