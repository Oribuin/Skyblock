package dev.oribuin.skyblock.island;

import org.bukkit.Chunk;
import org.bukkit.World;

/**
 * Represents a chunk position
 *
 * @param world  The world
 * @param chunkX The chunk X
 * @param chunkZ The chunk Z
 */
public record ChunkPosition(World world, int chunkX, int chunkZ) {

    /**
     * Create a new ChunkPosition instance
     *
     * @param chunk The chunk
     */
    public ChunkPosition(Chunk chunk) {
        this(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Get the chunk at the position
     *
     * @return The chunk
     */
    public Chunk getChunk() {
        return this.world.getChunkAt(this.chunkX, this.chunkZ);
    }

    /**
     * Check if the chunk is loaded
     *
     * @return If the chunk is loaded
     */
    public boolean isLoaded() {
        return this.world.isChunkLoaded(this.chunkX, this.chunkZ);
    }

}
