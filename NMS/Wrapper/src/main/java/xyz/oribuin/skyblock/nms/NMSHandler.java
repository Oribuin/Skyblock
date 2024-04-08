package xyz.oribuin.skyblock.nms;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface NMSHandler {

    /**
     * Send a worldborder to the center of a location to a specific player
     *
     * @param player The player viewing the worldborder
     * @param color  The color of the worldborder
     * @param size   The size of the worldborder
     * @param center The center location of the border.
     */
    void sendWorldBorder(Player player, BorderColor color, Double size, Location center);

    /**
     * Update chunks for all players on the server
     *
     * @param chunks  the chunk being updated
     * @param players The players the chunk is being updated for
     */
    void sendChunks(List<Chunk> chunks, List<Player> players);

    /**
     * Set an ItemStack NBT tag
     *
     * @param item  The ItemStack
     * @param key   The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */
    ItemStack setString(ItemStack item, String key, String value);

    /**
     * Set an ItemStack NBT tag
     *
     * @param item  The ItemStack
     * @param key   The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */
    ItemStack setInt(ItemStack item, String key, int value);

    /**
     * Set an ItemStack NBT tag
     *
     * @param item  The ItemStack
     * @param key   The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */
    ItemStack setLong(ItemStack item, String key, long value);

    /**
     * Set an ItemStack NBT tag
     *
     * @param item  The ItemStack
     * @param key   The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */
    ItemStack setDouble(ItemStack item, String key, double value);

    /**
     * Set an ItemStack NBT tag
     *
     * @param item  The ItemStack
     * @param key   The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */
    ItemStack setBoolean(ItemStack item, String key, boolean value);

}
