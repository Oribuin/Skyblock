package xyz.oribuin.skyblock.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import xyz.oribuin.skyblock.island.IslandBiome;
import xyz.oribuin.skyblock.util.PluginUtil;
import xyz.oribuin.skyblock.world.IslandSchematic;
import xyz.oribuin.skyblock.world.LayeredChunkGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldManager extends Manager {

    private final Map<World.Environment, String> worlds = new HashMap<>();
    private final Map<String, IslandSchematic> schematics = new HashMap<>();
    private final Map<Biome, IslandBiome> biomes = new HashMap<>();
    private CommentedFileConfiguration schemConfig;
    private CommentedFileConfiguration biomeConfig;

    public WorldManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        CommentedConfigurationSection worldSection = this.rosePlugin.getManager(ConfigurationManager.class)
                .getConfig()
                .getConfigurationSection("world-names");

        if (worldSection != null) {
            worldSection.getKeys(false).forEach(s -> {
                World.Environment environment = World.Environment.valueOf(s.toUpperCase());
                String name = worldSection.getString(s);

                this.worlds.put(environment, name);
            });
        }

        this.biomeConfig = CommentedFileConfiguration.loadConfiguration(PluginUtil.createFile(this.rosePlugin, "biomes.yml"));
        CommentedConfigurationSection biomeSection = this.biomeConfig.getConfigurationSection("biomes");
        if (biomeSection != null) {
            biomeSection.getKeys(false).forEach(key -> {
                Biome biome = PluginUtil.getEnum(Biome.class, key);
                String displayName = biomeSection.getString(key + ".display-name", key);
                Material icon = PluginUtil.getEnum(Material.class, biomeSection.getString(key + ".icon", "GRASS_BLOCK"));
                double cost = biomeSection.getDouble(key + ".cost", 0.0);

                this.biomes.put(PluginUtil.getEnum(Biome.class, key), new IslandBiome(biome, displayName, cost, icon));
            });
        }

        File schemFolder = new File(this.rosePlugin.getDataFolder(), "schematics");
        if (!schemFolder.exists()) {
            PluginUtil.createFile(this.rosePlugin, "schematics", "default.schem");
        }

        File schemFile = new File(this.rosePlugin.getDataFolder(), "schematics.yml");
        if (!schemFile.exists()) {
            PluginUtil.createFile(this.rosePlugin, "schematics.yml");
        }

        this.schemConfig = CommentedFileConfiguration.loadConfiguration(schemFile);
        CommentedConfigurationSection schemSection = this.schemConfig.getConfigurationSection("schematics");
        if (schemSection != null) {
            schemSection.getKeys(false).forEach(key -> {
                File file = new File(schemFolder, key + ".schem");
                if (!file.exists()) {
                    this.rosePlugin.getLogger().warning("Schematic file " + key + " does not exist.");
                    return;
                }

                String displayName = schemSection.getString(key + ".display-name", key);
                Material icon = PluginUtil.getEnum(Material.class, schemSection.getString(key + ".icon", "GRASS_BLOCK"));
                List<String> lore = schemSection.getStringList(key + ".lore");

                this.schematics.put(key, new IslandSchematic(file, key, displayName, icon, lore));
            });
        }

        this.worlds.forEach(this::createWorld);
    }

    /**
     * Create a new world using the island generator and return the world
     *
     * @param environment The environment of the world
     * @param name        The name of the world
     */
    public void createWorld(World.Environment environment, String name) {
        World world = Bukkit.getWorld(name);
        if (world != null) return;

        WorldCreator.name(name)
                .type(WorldType.FLAT)
                .environment(environment)
                .generateStructures(false)
                .generator(new LayeredChunkGenerator())
                .createWorld();
    }

    /**
     * Get a world from the environment
     *
     * @param environment the environment
     * @return the world
     */
    public World getWorld(World.Environment environment) {
        return Bukkit.getWorld(this.worlds.getOrDefault(environment, ""));
    }

    /**
     * Check if a location is inside an island world or not
     *
     * @param location The location to check
     * @return If the location is in an island world
     */
    public boolean isIslandWorld(Location location) {
        return this.worlds.containsValue(location.getWorld().getName());
    }

    public Map<Biome, IslandBiome> getBiomes() {
        return biomes;
    }

    public Map<World.Environment, String> getWorlds() {
        return worlds;
    }

    public Map<String, IslandSchematic> getSchematics() {
        return schematics;
    }


    @Override
    public void disable() {

    }

}
