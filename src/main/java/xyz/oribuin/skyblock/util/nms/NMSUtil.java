package xyz.oribuin.skyblock.util.nms;

import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.skyblock.island.member.BorderColor;

import java.util.List;

public final class NMSUtil {

    /**
     * Send a worldborder to the center of a location to a specific player
     *
     * @param player The player viewing the worldborder
     * @param color  The color of the worldborder
     * @param size   The size of the worldborder
     * @param center The center location of the border.
     */

    public static void sendWorldBorder(Player player, BorderColor color, double size, Location center) {
        WorldBorder worldBorder = new WorldBorder();
        worldBorder.world = ((CraftWorld) center.getWorld()).getHandle();
        worldBorder.setCenter(center.getX(), center.getZ());
        worldBorder.setWarningBlocks(0);
        worldBorder.setWarningTime(0);

        double newSize = size / 2;
        switch (color) {
            case OFF -> worldBorder.setSize(Double.MAX_VALUE);
            case BLUE -> worldBorder.setSize(newSize);
            case RED -> worldBorder.lerpSizeBetween(newSize, newSize - 1.0, 20000000L);
            case GREEN -> worldBorder.lerpSizeBetween(newSize - 1.0, newSize, 20000000L);
        }

        ((CraftPlayer) player).getHandle().connection.send(new ClientboundInitializeBorderPacket(worldBorder));
    }

    /**
     * Update chunks for all players on the server
     *
     * @param chunks  the chunk being updated
     * @param players The players the chunk is being updated for
     */

    public static void sendChunks(List<Chunk> chunks, List<Player> players) {
        List<ServerPlayer> nmsPlayers = players.stream().map(player -> ((CraftPlayer) player).getHandle()).toList();
        for (Chunk chunk : chunks) {
            ChunkAccess access = ((CraftChunk) chunk).getHandle(ChunkStatus.BIOMES);
            if (!(access instanceof LevelChunk levelChunk)) continue;

            ClientboundLevelChunkWithLightPacket packet = new ClientboundLevelChunkWithLightPacket(
                    levelChunk,
                    levelChunk.getLevel().getLightEngine(),
                    null,
                    null,
                    false
            );

            for (ServerPlayer player : nmsPlayers) {
                player.connection.send(packet);
            }
        }
    }

    /**
     * Set an ItemStack NBT tag
     *
     * @param item  The ItemStack
     * @param key   The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */

    public static ItemStack setString(ItemStack item, String key, String value) {
        net.minecraft.world.item.ItemStack craftItemStack = CraftItemStack.asNMSCopy(item);
        craftItemStack.getOrCreateTag().putString(key, value);
        return CraftItemStack.asBukkitCopy(craftItemStack);
    }

    /**
     * Set an ItemStack NBT tag
     *
     * @param item  The ItemStack
     * @param key   The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */

    public static ItemStack setInt(ItemStack item, String key, int value) {
        net.minecraft.world.item.ItemStack craftItemStack = CraftItemStack.asNMSCopy(item);
        craftItemStack.getOrCreateTag().putInt(key, value);
        return CraftItemStack.asBukkitCopy(craftItemStack);
    }

    /**
     * Set an ItemStack NBT tag
     *
     * @param item  The ItemStack
     * @param key   The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */

    public static ItemStack setLong(ItemStack item, String key, long value) {
        net.minecraft.world.item.ItemStack craftItemStack = CraftItemStack.asNMSCopy(item);
        craftItemStack.getOrCreateTag().putLong(key, value);
        return CraftItemStack.asBukkitCopy(craftItemStack);
    }

    /**
     * Set an ItemStack NBT tag
     *
     * @param item  The ItemStack
     * @param key   The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */

    public static ItemStack setDouble(ItemStack item, String key, double value) {
        net.minecraft.world.item.ItemStack craftItemStack = CraftItemStack.asNMSCopy(item);
        craftItemStack.getOrCreateTag().putDouble(key, value);
        return CraftItemStack.asBukkitCopy(craftItemStack);
    }

    /**
     * Set an ItemStack NBT tag
     *
     * @param item  The ItemStack
     * @param key   The key to the tag
     * @param value The key value
     * @return The modified itemstack.
     */

    public static ItemStack setBoolean(ItemStack item, String key, boolean value) {
        net.minecraft.world.item.ItemStack craftItemStack = CraftItemStack.asNMSCopy(item);
        craftItemStack.getOrCreateTag().putBoolean(key, value);
        return CraftItemStack.asBukkitCopy(craftItemStack);
    }

}
