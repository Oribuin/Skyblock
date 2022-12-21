package xyz.oribuin.skyblock.nms

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface NMSHandler {

    /**
     * Send a worldborder to the center of a location to a specific player
     *
     * @param player The player viewing the worldborder
     * @param color The color of the worldborder
     * @param size The size of the worldborder
     * @param center The center location of the border.
     */
    fun sendWorldBorder(player: Player, color: BorderColor, size: Double, center: Location)

    /**
     * Update chunks for all players on the server
     *
     * @param chunks the chunk being updated
     * @param players The players the chunk is being updated for
     */
    fun sendChunks(chunks: List<Chunk>, players: List<Player>)

    /**
     * Set an ItemStack NBT tag
     *
     * @param item The ItemStack
     * @param key The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */
    fun setString(item: ItemStack, key: String, value: String): ItemStack

    /**
     * Set an ItemStack NBT tag
     *
     * @param item The ItemStack
     * @param key The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */
    fun setInt(item: ItemStack, key: String, value: Int): ItemStack

    /**
     * Set an ItemStack NBT tag
     *
     * @param item The ItemStack
     * @param key The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */
    fun setLong(item: ItemStack, key: String, value: Long): ItemStack

    /**
     * Set an ItemStack NBT tag
     *
     * @param item The ItemStack
     * @param key The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */
    fun setDouble(item: ItemStack, key: String, value: Double): ItemStack

    /**
     * Set an ItemStack NBT tag
     *
     * @param item The ItemStack
     * @param key The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */
    fun setBoolean(item: ItemStack, key: String, value: Boolean): ItemStack

//    /**
//     * Serialize an ItemStack to a byte array
//     */
//    fun serialize(item: ItemStack): ByteArray
//
//    /**
//     * Deserialize an ItemStack from a byte array
//     */
//    fun deserialize(bytes: ByteArray): ItemStack

}