package xyz.oribuin.skyblock.world

import org.bukkit.World
import org.bukkit.block.Biome
import org.bukkit.generator.ChunkGenerator
import java.util.*

class VoidGenerator : ChunkGenerator() {

    override fun generateChunkData(world: World, random: Random, chunkX: Int, chunkZz: Int, grid: BiomeGrid): ChunkData {
        val biome = when (world.environment) {
            World.Environment.NETHER -> Biome.NETHER_WASTES
            World.Environment.THE_END -> Biome.THE_END
            else -> Biome.PLAINS
        }
        for (x in 0..15 step 4)
            for (z in 0..15 step 4)
                for (y in 0..world.maxHeight step 4)
                    grid.setBiome(x, y, z, biome)

        return this.createChunkData(world)
    }

    override fun canSpawn(world: World, x: Int, z: Int): Boolean = false

    override fun isParallelCapable(): Boolean = true

}