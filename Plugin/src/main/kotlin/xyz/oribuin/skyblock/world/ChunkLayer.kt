package xyz.oribuin.skyblock.world

import org.bukkit.Material
import org.bukkit.generator.ChunkGenerator

data class ChunkLayer(val startLayer: Int, val endLayer: Int, val material: Material) {

    /**
     * Fill the chunk with the specified material
     *
     * @param chunkData The data to fill the chunk with
     */
    fun fill(chunkData: ChunkGenerator.ChunkData) = chunkData.setRegion(0, this.startLayer, 0, 16, this.endLayer + 1, 16, this.material)

}