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
import xyz.oribuin.skyblock.util.SkyblockUtil;
import xyz.oribuin.skyblock.world.IslandSchematic;
import xyz.oribuin.skyblock.world.LayeredChunkGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldManager extends Manager {

    private final Map<World.Environment, String> worlds = new HashMap<>();
    private final Map<String, IslandSchematic> schematics = new HashMap<>();
    private CommentedFileConfiguration config;

    public WorldManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        CommentedConfigurationSection worldSection = this.config.getConfigurationSection("world-names");
        if (worldSection != null) {
            worldSection.getKeys(false).forEach(s -> {
                World.Environment environment = World.Environment.valueOf(s.toUpperCase());
                String name = worldSection.getString(s);
                this.worlds.put(environment, name);
            });
        }

        File schemFolder = new File(this.rosePlugin.getDataFolder(), "schematics");
        if (!schemFolder.exists()) schemFolder.mkdirs();

        File schemFile = new File(this.rosePlugin.getDataFolder(), "schematics.yml");
        if (!schemFile.exists()) {
            SkyblockUtil.createFile(this.rosePlugin, "schematics.yml");
        }

        this.config = CommentedFileConfiguration.loadConfiguration(schemFile);
        CommentedConfigurationSection schemSection = this.config.getConfigurationSection("schematics");
        if (schemSection != null) {
            schemSection.getKeys(false).forEach(key -> {
                File file = new File(schemFolder, key + ".schem");
                if (!file.exists()) {
                    this.rosePlugin.getLogger().warning("Schematic file " + key + " does not exist.");
                    return;
                }

                String displayName = schemSection.getString(key + ".display-name", key);
                Material icon = SkyblockUtil.getEnum(Material.class, schemSection.getString(key + ".icon", "GRASS_BLOCK"));
                List<String> lore = schemSection.getStringList(key + ".lore");

                this.schematics.put(key, new IslandSchematic(file, key, displayName, icon, lore));
            });
        }

    }

    /**
     * Create a new world using the island generator and return the world
     *
     * @param name        The name of the world
     * @param environment The environment of the world
     * @return The world
     */
    public World createWorld(String name, World.Environment environment) {
        World world = Bukkit.getWorld(name);
        if (world != null) return world;

        return WorldCreator.name(name)
                .type(WorldType.FLAT)
                .environment(environment)
                .generateStructures(false)
                .generator(new LayeredChunkGenerator())
                .createWorld();
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

    @Override
    public void disable() {

    }

}
