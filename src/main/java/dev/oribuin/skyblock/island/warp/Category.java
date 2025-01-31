package dev.oribuin.skyblock.island.warp;

import org.bukkit.Material;

import java.util.List;

public enum Category {

    GENERAL(Material.NAME_TAG, List.of(" <white>| <gray>General islands with", " <white>| <gray>multiple purposes."), 12),
    FARMS(Material.DIAMOND_HOE, List.of(" <white>| <gray>Islands with public farms", " <white>| <gray>for anyone to use."), 13),
    PARKOUR(Material.FEATHER, List.of(" <white>| <gray>Islands with a focus", " <white>| <gray>on their fun parkour"), 14),
    SHOPS(Material.SPRUCE_SIGN, List.of(" <white>| <gray>Islands with shops for", " <white>| <gray>anyone to buy/sell at."), 15),
    DESIGN(Material.PINK_TULIP, List.of(" <white>| <gray>Islands that are focused", " <white>| <gray>on their design aesthetic"), 16);

    private final Material icon;
    private final List<String> desc;
    private final int slot;

    /**
     * Create a new instance of a warp category
     *
     * @param icon The icon of the category
     * @param desc The description of the category
     * @param slot The slot of the category
     */
    Category(Material icon, List<String> desc, int slot) {
        this.icon = icon;
        this.desc = desc;
        this.slot = slot;
    }

    public Material getIcon() {
        return icon;
    }

    public List<String> getDesc() {
        return desc;
    }

    public int getSlot() {
        return slot;
    }

}
