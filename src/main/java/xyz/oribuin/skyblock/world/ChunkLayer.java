package xyz.oribuin.skyblock.world;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;

public record ChunkLayer(int startLayer, int endLayer, Material material) {

    /**
     * Fill the chunk with the specified material
     *
     * @param chunkData The data to fill the chunk with
     */
    public void fill(ChunkGenerator.ChunkData chunkData) {
        chunkData.setRegion(0, this.startLayer, 0, 16, this.endLayer + 1, 16, this.material);
    }

}