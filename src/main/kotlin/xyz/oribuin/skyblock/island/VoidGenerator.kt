package xyz.oribuin.skyblock.island

import org.bukkit.World
import org.bukkit.block.Biome
import org.bukkit.generator.ChunkGenerator
import xyz.oribuin.skyblock.library.NMSUtil
import java.util.*

/**
 * @author Esophose
 */
class VoidGenerator : ChunkGenerator() {

    override fun generateChunkData(world: World, random: Random, chunkX: Int, chunkZ: Int, biomeGrid: BiomeGrid): ChunkData {
        val biome = when (world.environment) {
            World.Environment.NORMAL -> Biome.PLAINS
            World.Environment.NETHER -> if (NMSUtil.versionNumber >= 16) Biome.NETHER_WASTES else Biome.valueOf("NETHER")
            World.Environment.THE_END -> Biome.THE_END
        }

        // 1.16 has 3d biomes, while older versions do not
        // 3d biomes are separated into 4x4 pieces, so there are 64 pieces per chunk section, or 1024 per chunk
        if (NMSUtil.versionNumber >= 16) {
            for (x in 0..15 step 4)
                for (z in 0..15 step 4)
                    for (y in 0..world.maxHeight step 4)
                        biomeGrid.setBiome(x, y, z, biome)
        } else {
            @Suppress("DEPRECATION")
            for (x in 0..15)
                for (z in 0..15)
                    biomeGrid.setBiome(x, z, biome)
        }

        return this.createChunkData(world)
    }

    override fun canSpawn(world: World, x: Int, z: Int): Boolean {
        return false
    }

    override fun isParallelCapable(): Boolean {
        return true
    }

}
