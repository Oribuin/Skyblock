package xyz.oribuin.skyblock.island.warp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Warp {

    private final int key;
    private String name;
    private Location location;
    private ItemStack icon;
    private Category category;

    /**
     * Create a new instance of a warp object
     *
     * @param key      The key of the island associated with teh warp
     * @param location The location of the warp
     */
    public Warp(int key, Location location, String name) {
        this.key = key;
        this.name = name;
        this.location = location;
        this.icon = new ItemStack(Material.GRASS_BLOCK);
        this.category = Category.GENERAL;
    }

    public int getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

}
