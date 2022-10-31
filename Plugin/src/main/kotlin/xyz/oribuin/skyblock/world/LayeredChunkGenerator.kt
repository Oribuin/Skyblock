package xyz.oribuin.skyblock.world

import java.util.*
import org.bukkit.Material
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo

class LayeredChunkGenerator : ChunkGenerator() {

    /// TODO: Add support for biomes
    override fun generateSurface(worldInfo: WorldInfo, random: Random, x: Int, z: Int, chunkData: ChunkData) =
        chunkData.setRegion(0, worldInfo.minHeight, 0, 16, worldInfo.maxHeight, 16, Material.AIR)

    override fun generateCaves(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) =
        chunkData.setRegion(0, worldInfo.minHeight, 0, 16, worldInfo.maxHeight, 16, Material.AIR)

    override fun generateBedrock(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) =
        chunkData.setRegion(0, worldInfo.minHeight, 0, 16, worldInfo.maxHeight, 16, Material.AIR)

    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) =
        chunkData.setRegion(0, worldInfo.minHeight, 0, 16, worldInfo.maxHeight, 16, Material.AIR)
}