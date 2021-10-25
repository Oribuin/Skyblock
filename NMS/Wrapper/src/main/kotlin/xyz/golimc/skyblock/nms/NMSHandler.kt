package xyz.golimc.skyblock.nms

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

}