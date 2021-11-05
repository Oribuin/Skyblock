package xyz.oribuin.skyblock.nms

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Player

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

}