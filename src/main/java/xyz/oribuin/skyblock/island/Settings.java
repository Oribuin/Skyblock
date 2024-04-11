package xyz.oribuin.skyblock.island;

import org.bukkit.block.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Settings {

    private final int key;
    private String islandName;
    private boolean publicIsland;
    private boolean mobSpawning;
    private boolean animalSpawning;
    private Biome biome;
    private List<UUID> banned;

    /**
     * Create a new Settings instance
     *
     * @param key The island key
     */
    public Settings(int key, String islandName) {
        this.key = key;
        this.islandName = islandName;
        this.publicIsland = true;
        this.mobSpawning = true;
        this.animalSpawning = true;
        this.biome = Biome.PLAINS;
        this.banned = new ArrayList<>();
    }

    public int getKey() {
        return key;
    }

    public String getIslandName() {
        return islandName;
    }

    public void setIslandName(String islandName) {
        this.islandName = islandName;
    }

    public boolean isPublicIsland() {
        return publicIsland;
    }

    public void setPublicIsland(boolean publicIsland) {
        this.publicIsland = publicIsland;
    }

    public boolean isMobSpawning() {
        return mobSpawning;
    }

    public void setMobSpawning(boolean mobSpawning) {
        this.mobSpawning = mobSpawning;
    }

    public boolean isAnimalSpawning() {
        return animalSpawning;
    }

    public void setAnimalSpawning(boolean animalSpawning) {
        this.animalSpawning = animalSpawning;
    }

    public Biome getBiome() {
        return biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public List<UUID> getBanned() {
        return banned;
    }

    public void setBanned(List<UUID> banned) {
        this.banned = banned;
    }

}