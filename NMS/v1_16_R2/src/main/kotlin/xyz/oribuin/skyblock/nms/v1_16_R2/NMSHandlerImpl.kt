package xyz.oribuin.skyblock.nms.v1_16_R2

import net.minecraft.server.v1_16_R2.PacketPlayOutMapChunk
import net.minecraft.server.v1_16_R2.PacketPlayOutWorldBorder
import net.minecraft.server.v1_16_R2.WorldBorder
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R2.CraftChunk
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.nms.BorderColor
import xyz.oribuin.skyblock.nms.NMSHandler

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
        border.warningDistance = 0
        if (color == BorderColor.RED)
            border.transitionSizeBetween(size, size - 1.0, 20000000L)
        else if (color == BorderColor.GREEN)
            border.transitionSizeBetween(size - 1.0, size, 20000000L)

        ((player as CraftPlayer).handle.playerConnection.sendPacket(PacketPlayOutWorldBorder(border, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE)))
    }

    override fun sendChunks(chunks: List<Chunk>, players: List<Player>) {
        chunks.forEach { chunk ->
            val packet = PacketPlayOutMapChunk((chunk as CraftChunk).handle, 65535)
            players.map { (it as CraftPlayer).handle }.forEach { it.playerConnection.sendPacket(packet) }
        }
    }

}