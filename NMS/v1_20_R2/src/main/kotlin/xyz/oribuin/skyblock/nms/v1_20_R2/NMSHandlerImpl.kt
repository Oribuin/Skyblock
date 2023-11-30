package xyz.oribuin.skyblock.nms.v1_20_R2

import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket
import net.minecraft.world.level.border.WorldBorder
import net.minecraft.world.level.chunk.ChunkStatus
import net.minecraft.world.level.chunk.LevelChunk
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_20_R2.CraftChunk
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.oribuin.skyblock.nms.BorderColor
import xyz.oribuin.skyblock.nms.BorderColor.*
import xyz.oribuin.skyblock.nms.NMSHandler


class NMSHandlerImpl : NMSHandler {

    override fun sendWorldBorder(player: Player, color: BorderColor, size: Double, center: Location) {
        val border = WorldBorder()
        border.world = (center.world as CraftWorld).handle
        border.warningBlocks = 0
        border.warningTime = 0
        border.setCenter(center.blockX + 0.5, center.blockZ + 0.5)

        when (color) {
            OFF -> border.size = Double.MAX_VALUE
            RED -> border.lerpSizeBetween(size, size - 1.0, 20000000L)
            GREEN -> border.lerpSizeBetween(size - 1.0, size, 20000000L)
            BLUE -> border.size = size
        }

        (player as CraftPlayer).handle.connection.send(ClientboundInitializeBorderPacket(border))
    }

    override fun sendChunks(chunks: List<Chunk>, players: List<Player>) {
        val nmsPlayers = players.map { (it as CraftPlayer).handle }
        chunks.forEach { chunk ->

            val nmsChunk = (chunk as CraftChunk).getHandle(ChunkStatus.BIOMES)
            if (nmsChunk !is LevelChunk) return@forEach

            val packet = ClientboundLevelChunkWithLightPacket(
                nmsChunk,
                nmsChunk.level.lightEngine,
                null,
                null
            )

            nmsPlayers.forEach { it.connection.send(packet) }
        }
    }

    override fun setString(item: ItemStack, key: String, value: String): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        itemStack.orCreateTag.putString(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

    override fun setInt(item: ItemStack, key: String, value: Int): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        itemStack.orCreateTag.putInt(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

    override fun setLong(item: ItemStack, key: String, value: Long): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        itemStack.orCreateTag.putLong(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

    override fun setDouble(item: ItemStack, key: String, value: Double): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        itemStack.orCreateTag.putDouble(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

    override fun setBoolean(item: ItemStack, key: String, value: Boolean): ItemStack {
        val itemStack = CraftItemStack.asNMSCopy(item)
        itemStack.orCreateTag.putBoolean(key, value)
        return CraftItemStack.asBukkitCopy(itemStack)
    }

}