package xyz.oribuin.skyblock.island;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import xyz.oribuin.skyblock.util.nms.NMSUtil;

import java.util.List;

public class IslandBiome {

    private final Biome biome;
    private String displayName;
    private double cost;
    private Material icon;

    /**
     * Create a new Biome Option
     *
     * @param biome The biome
     * @param cost  The cost
     * @param icon  The icon
     */
    public IslandBiome(Biome biome, String displayName, double cost, Material icon) {
        this.biome = biome;
        this.displayName = displayName;
        this.cost = cost;
        this.icon = icon;
    }

    /**
     * Create a new Biome Option with default values
     *
     * @param biome The biome
     */
    public IslandBiome(Biome biome) {
        this(biome, biome.name(), 1000.0, Material.GRASS_BLOCK);
    }

    /**
     * Apply the biome to all the players on the island.
     *
     * @param island The island
     */
    public void apply(Island island) {
        List<Chunk> chunks = island.getChunks().stream().filter(ChunkPosition::isLoaded)
                .map(ChunkPosition::getChunk)
                .toList();

        // Update all the blocks within the chunk
        for (Chunk chunk : chunks) {
            int minY = chunk.getWorld().getMinHeight();
            int maxY = chunk.getWorld().getMaxHeight();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = minY; y < maxY; y++) {
                        chunk.getBlock(x, y, z).setBiome(this.biome);
                    }
                }
            }
        }

        // Send the update to the players on the island
        List<Player> players = island.getCenter().getNearbyPlayers(island.getSize()).stream().toList();
        NMSUtil.sendChunks(chunks, players);
    }

    public Biome getBiome() {
        return this.biome;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Material getIcon() {
        return this.icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

}