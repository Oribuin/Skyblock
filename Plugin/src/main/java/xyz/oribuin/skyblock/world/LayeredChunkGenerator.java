package xyz.oribuin.skyblock.world;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LayeredChunkGenerator extends ChunkGenerator {

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
        chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, worldInfo.getMaxHeight(), 16, Material.AIR);
    }

    @Override
    public void generateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
        chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, worldInfo.getMaxHeight(), 16, Material.AIR);
    }

    @Override
    public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
        chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, worldInfo.getMaxHeight(), 16, Material.AIR);
    }

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
        chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, worldInfo.getMaxHeight(), 16, Material.AIR);
    }

}