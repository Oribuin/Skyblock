package xyz.golimc.skyblock.nms.v1_17_R1

import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket
import net.minecraft.world.level.border.WorldBorder
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import xyz.golimc.skyblock.nms.BorderColor
import xyz.golimc.skyblock.nms.NMSHandler

class NMSHandlerImpl : NMSHandler {

    override fun sendWorldBorder(player: Player, color: BorderColor, size: Double, center: Location) {
        val border = WorldBorder()
        border.world = (center.world as CraftWorld).handle
        border.setCenter(center.blockX + 0.5, center.blockZ + 0.5)

        if (color == BorderColor.OFF)
            border.size = Double.MAX_VALUE
        else
            border.size = size

        border.warningTime = 0
        border.warningBlocks = 0
        if (color == BorderColor.RED)
            border.lerpSizeBetween(size, size - 1.0, 20000000L)
        else if (color == BorderColor.GREEN)
            border.lerpSizeBetween(size - 1.0, size, 20000000L)

        (player as CraftPlayer).handle.connection.send(ClientboundInitializeBorderPacket(border))
    }

}