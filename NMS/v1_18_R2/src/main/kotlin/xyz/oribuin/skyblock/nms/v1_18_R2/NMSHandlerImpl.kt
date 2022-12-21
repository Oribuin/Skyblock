package xyz.oribuin.skyblock.nms.v1_18_R2

import javax.print.DocFlavor.STRING
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket
import net.minecraft.world.level.border.WorldBorder
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_18_R2.CraftChunk
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack
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
        border.warningBlocks = 0
        if (color == BorderColor.RED)
            border.lerpSizeBetween(size, size - 1.0, 20000000L)
        else if (color == BorderColor.GREEN)
            border.lerpSizeBetween(size - 1.0, size, 20000000L)

        (player as CraftPlayer).handle.connection.send(ClientboundInitializeBorderPacket(border))
    }

    override fun sendChunks(chunks: List<Chunk>, players: List<Player>) {
        val nmsPlayers = players.map { (it as CraftPlayer).handle }
        chunks.forEach { chunk ->
            val levelChunk = (chunk as CraftChunk).handle
            val packet = ClientboundLevelChunkWithLightPacket(levelChunk, levelChunk.level.lightEngine, null, null, true)
            nmsPlayers.forEach { it.connection.send(packet) }
        }
    }

    override fun test(itemStack: ItemStack): STRING {
        return CraftItemStack.asNMSCopy(itemStack).tag.toString();
    }

    override fun setString(item: ItemStack, key: String, value: String): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        val tag = itemStack.orCreateTag
        tag.putString(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

    override fun setInt(item: ItemStack, key: String, value: Int): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        val tag = itemStack.orCreateTag
        tag.putInt(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

    override fun setLong(item: ItemStack, key: String, value: Long): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        val tag = itemStack.orCreateTag
        tag.putLong(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

    override fun setDouble(item: ItemStack, key: String, value: Double): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        val tag = itemStack.orCreateTag
        tag.putDouble(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

    override fun setBoolean(item: ItemStack, key: String, value: Boolean): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        val tag = itemStack.orCreateTag
        tag.putBoolean(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

}