package xyz.oribuin.skyblock.nms.v1_16_R3

import net.minecraft.server.v1_16_R3.PacketPlayOutMapChunk
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder
import net.minecraft.server.v1_16_R3.WorldBorder
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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

    override fun setString(item: ItemStack, key: String, value: String): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        val tag = itemStack.orCreateTag
        tag.setString(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

    override fun setInt(item: ItemStack, key: String, value: Int): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        val tag = itemStack.orCreateTag
        tag.setInt(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

    override fun setLong(item: ItemStack, key: String, value: Long): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        val tag = itemStack.orCreateTag
        tag.setLong(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

    override fun setDouble(item: ItemStack, key: String, value: Double): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        val tag = itemStack.orCreateTag
        tag.setDouble(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

    override fun setBoolean(item: ItemStack, key: String, value: Boolean): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        val tag = itemStack.orCreateTag
        tag.setBoolean(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

}