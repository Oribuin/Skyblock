package dev.oribuin.skyblock.manager;

import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.oribuin.skyblock.database.migration.CreateInitialTables;
import dev.oribuin.skyblock.island.Island;
import dev.oribuin.skyblock.island.Settings;
import dev.oribuin.skyblock.island.member.BorderColor;
import dev.oribuin.skyblock.island.member.Member;
import dev.oribuin.skyblock.island.member.Role;
import dev.oribuin.skyblock.island.warp.Category;
import dev.oribuin.skyblock.island.warp.Warp;
import dev.oribuin.skyblock.util.SkyblockUtil;
import dev.oribuin.skyblock.util.serializer.UUIDSerialized;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataManager extends AbstractDataManager {

    private static final Gson GSON = new Gson();
    private final Map<Integer, Island> islandCache = new HashMap<>();
    private final Map<UUID, Member> userCache = new HashMap<>();

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        // Save any "dirty" islands every minute
        Bukkit.getScheduler().runTaskTimerAsynchronously(rosePlugin, () -> this.islandCache.values()
                        .stream()
                        .filter(Island::isDirty)
                        .forEach(this::saveIsland)
                , 20 * 60, 20 * 60);
    }

    @Override
    public void reload() {
        super.reload();
        this.async(() -> this.databaseConnector.connect(connection -> {
            String selectMembers = "SELECT * FROM " + this.getTablePrefix() + "members";

            // Load all the islands from the database
            String selectIslands = "SELECT * FROM " + this.getTablePrefix() + "islands";
            try (PreparedStatement statement = connection.prepareStatement(selectIslands)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Island island = this.constructIsland(resultSet);
                    if (island == null) return;
                    island.setDirty(false);
                    this.islandCache.put(island.getKey(), island);
                }
            }

            // Load all the players and put them in their associated island
            try (PreparedStatement statement = connection.prepareStatement(selectMembers)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Member member = this.constructMember(resultSet);
                    if (member == null) return;

                    if (member.getIsland() > -1) {
                        Island island = this.islandCache.get(member.getIsland());
                        if (island != null) {
                            island.getMembers().add(member.getUUID());
                            this.islandCache.put(island.getKey(), island);
                        }
                    }

                    this.userCache.put(member.getUUID(), member);
                }
            }
            // Load all the warps from the database
            String selectWarps = "SELECT * FROM " + this.getTablePrefix() + "warps";
            try (PreparedStatement statement = connection.prepareStatement(selectWarps)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Warp warp = this.constructWarp(resultSet);
                    if (warp == null) return;

                    Island island = this.islandCache.get(warp.getKey());
                    if (island != null) {
                        island.setWarp(warp);
                        this.islandCache.put(island.getKey(), island);
                    }
                }
            }

            // Load all the settings from the database
            String selectSettings = "SELECT * FROM " + this.getTablePrefix() + "settings";
            try (PreparedStatement statement = connection.prepareStatement(selectSettings)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Settings settings = this.constructSettings(resultSet);
                    if (settings == null) return;

                    Island island = this.islandCache.get(settings.getKey());
                    if (island != null) {
                        island.setSettings(settings);
                        this.islandCache.put(island.getKey(), island);
                    }
                }
            }

            // Load all the homes from the database
            String selectHomes = "SELECT * FROM " + this.getTablePrefix() + "homes";
            try (PreparedStatement statement = connection.prepareStatement(selectHomes)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Location location = this.constructLocation(resultSet);
                    if (location == null) return;

                    Island island = this.islandCache.get(resultSet.getInt("key"));
                    if (island != null) {
                        island.setHome(location);
                        this.islandCache.put(island.getKey(), island);
                    }
                }
            }
        }));
    }

    @Override
    public void disable() {
        CompletableFuture.runAsync(() -> {
            this.islandCache.values().forEach(this::saveIsland);
            this.userCache.values().forEach(this::saveMember);
        }).thenAccept(x -> super.disable());
    }

    /**
     * Get an island from the island key
     *
     * @param key The island key
     * @return The island
     */
    @Nullable
    public Island getIsland(int key) {
        return this.islandCache.get(key);
    }

    /**
     * Get an island from its member
     *
     * @param member The island owner
     * @return The island
     */
    @Nullable
    public Island getIsland(@NotNull UUID member) {
        return this.islandCache.values()
                .stream()
                .filter(island -> island.getMembers().contains(member))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get island by the location using chunk pdc
     *
     * @param location The location
     * @return The island if available
     */
    @Nullable
    public Island getIsland(@NotNull Location location) {
        return this.islandCache.values()
                .stream()
                .filter(island -> island.isWithinBounds(location))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get a member from the database
     *
     * @param uuid The player's uuid
     * @return the member
     */
    @NotNull
    public Member getMember(UUID uuid) {
        return this.userCache.getOrDefault(uuid, new Member(uuid));
    }

    /**
     * Cache an island into the plugin
     *
     * @param island The island to cache
     */
    public void cache(Island island) {
        island.setDirty(true);
        this.islandCache.put(island.getKey(), island);
    }

    /**
     * Create a brand-new island in the plugin database
     *
     * @param owner The owner of the island
     */
    public void createIsland(Player owner, Consumer<Island> result) {

        this.async(() -> this.databaseConnector.connect(connection -> {
            String createIsland = "INSERT INTO " + this.getTablePrefix() + "islands (owner) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(createIsland, Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, owner.toString());
                statement.executeUpdate();

                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    int key = keys.getInt(1);
                    Island island = new Island(key, owner.getUniqueId());
                    this.saveIsland(island, result);

                    // Update the member's island
                    Member member = this.getMember(owner.getUniqueId());
                    member.setIsland(key);
                    this.saveMember(member);

                    // callback :-3
                    result.accept(island);
                }
            }
        }));
    }

    /**
     * Save an island into the database and cache with no callback
     *
     * @param island The island to save
     */
    public void saveIsland(Island island) {
        this.saveIsland(island, x -> {
            // now what if this did something
        });
    }

    /**
     * Save an island into the database
     *
     * @param island   The island to save
     * @param callback The result of the saving
     */
    public void saveIsland(Island island, Consumer<Island> callback) {
        island.setDirty(false);
        this.islandCache.put(island.getKey(), island);

        this.async(() -> this.databaseConnector.connect(connection -> {
            // Update Primary Data

            String replaceIsland = "REPLACE INTO " + this.getTablePrefix() + "islands (`key`, owner, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement updateIsland = connection.prepareStatement(replaceIsland)) {
                updateIsland.setInt(1, island.getKey());
                updateIsland.setString(2, island.getOwner().toString());
                updateIsland.setDouble(3, island.getCenter().getBlockX());
                updateIsland.setDouble(4, island.getCenter().getBlockY());
                updateIsland.setDouble(5, island.getCenter().getBlockZ());
                updateIsland.setFloat(6, island.getCenter().getYaw());
                updateIsland.setFloat(7, island.getCenter().getPitch());
                updateIsland.setString(8, island.getCenter().getWorld().getName());
                updateIsland.executeUpdate();
            }

            // Update Island Settings
            String replaceSettings = "REPLACE INTO " + this.getTablePrefix() + "settings (`key`, `name`, `public`, mobSpawning, animalSpawning, biome, bans) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement updateSettings = connection.prepareStatement(replaceSettings)) {
                updateSettings.setInt(1, island.getKey());
                updateSettings.setString(1, island.getSettings().getIslandName());
                updateSettings.setBoolean(3, island.getSettings().isPublicIsland());
                updateSettings.setBoolean(4, island.getSettings().isMobSpawning());
                updateSettings.setBoolean(5, island.getSettings().isAnimalSpawning());
                updateSettings.setString(6, island.getSettings().getBiome().key().toString());
                updateSettings.setString(7, GSON.toJson(new UUIDSerialized(island.getSettings().getBanned())));
                updateSettings.executeUpdate();
            }

            // Update Island warp
            String replaceWarp = "REPLACE INTO " + this.getTablePrefix() + "warps (`key`, `name`, icon, category, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement updateWarp = connection.prepareStatement(replaceWarp)) {
                updateWarp.setInt(1, island.getKey());
                updateWarp.setString(2, island.getWarp().getName());
                updateWarp.setBytes(3, island.getWarp().getIcon().serializeAsBytes());
                updateWarp.setString(4, island.getWarp().getCategory().name());
                updateWarp.setDouble(5, island.getWarp().getLocation().getX());
                updateWarp.setDouble(6, island.getWarp().getLocation().getY());
                updateWarp.setDouble(7, island.getWarp().getLocation().getZ());
                updateWarp.setFloat(8, island.getWarp().getLocation().getYaw());
                updateWarp.setFloat(9, island.getWarp().getLocation().getPitch());
                updateWarp.setString(10, island.getWarp().getLocation().getWorld().getName());
                updateWarp.executeUpdate();
            }


            String replaceHome = "REPLACE INTO " + this.getTablePrefix() + "homes (`key`, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement updateHome = connection.prepareStatement(replaceHome)) {
                updateHome.setInt(1, island.getKey());
                updateHome.setDouble(2, island.getWarp().getLocation().getX());
                updateHome.setDouble(3, island.getWarp().getLocation().getY());
                updateHome.setDouble(4, island.getWarp().getLocation().getZ());
                updateHome.setFloat(5, island.getWarp().getLocation().getYaw());
                updateHome.setFloat(6, island.getWarp().getLocation().getPitch());
                updateHome.setString(7, island.getWarp().getLocation().getWorld().getName());
                updateHome.executeUpdate();
            }

            callback.accept(island);
        }));
    }

    /**
     * Save a member from the cache using their uuid
     *
     * @param uuid The Player UUID
     */
    public void saveMember(UUID uuid) {
        this.saveMember(this.userCache.getOrDefault(uuid, new Member(uuid)));
    }

    /**
     * Save the member to the database
     *
     * @param member The member
     */
    public void saveMember(Member member) {
        this.userCache.put(member.getUUID(), member);

        this.async(() -> this.databaseConnector.connect(connection -> {
            String insert = "REPLACE INTO " + this.getTablePrefix() + "members (`player`, `username`, `key`, `role`, `border`) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement statement = connection.prepareStatement(insert)) {
                statement.setString(1, member.getUUID().toString());
                statement.setString(2, member.getUsername());
                statement.setInt(3, member.getIsland());
                statement.setString(4, member.getRole().name());
                statement.setString(5, member.getBorder().name());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Load a member from the database and cache them.
     *
     * @param uuid The UUID to load
     */
    public void loadMember(UUID uuid) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String select = "SELECT * FROM " + this.getTablePrefix() + "members WHERE player = ?";
            try (PreparedStatement statement = connection.prepareStatement(select)) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    Member member = this.constructMember(resultSet);
                    if (member == null) member = new Member(uuid);

                    this.userCache.put(uuid, member);
                }
            }
        }));
    }

    /**
     * Construct a location from a result set
     *
     * @param resultSet The result set
     * @return The location
     * @throws SQLException If an error occurs
     */
    public Location constructLocation(ResultSet resultSet) throws SQLException {
        return new Location(
                Bukkit.getWorld(resultSet.getString("world")),
                resultSet.getDouble("x"),
                resultSet.getDouble("y"),
                resultSet.getDouble("z"),
                resultSet.getFloat("yaw"),
                resultSet.getFloat("pitch")
        );
    }

    /**
     * Construct a member from a result set from the database
     *
     * @param resultSet The result set
     * @return The member
     * @throws SQLException If an error occurs
     */
    public Member constructMember(ResultSet resultSet) throws SQLException {
        Member member = new Member(UUID.fromString(resultSet.getString("player")));
        member.setIsland(resultSet.getInt("key"));
        member.setUsername(resultSet.getString("username"));
        member.setRole(Role.valueOf(resultSet.getString("role").toUpperCase()));
        member.setBorder(BorderColor.valueOf(resultSet.getString("border").toUpperCase()));
        return member;
    }

    /**
     * Construct an island from a result set from the database
     *
     * @param resultSet The result set
     * @return The island
     * @throws SQLException If an error occurs
     */
    public Island constructIsland(ResultSet resultSet) throws SQLException {
        return new Island(
                resultSet.getInt("key"),
                constructLocation(resultSet),
                UUID.fromString(resultSet.getString("owner"))
        );
    }

    /**
     * Construct a warp from a result set from the database
     *
     * @param resultSet The result set
     * @return The warp
     * @throws SQLException If an error occurs
     */
    public Warp constructWarp(ResultSet resultSet) throws SQLException {
        int key = resultSet.getInt("key");
        Category category = SkyblockUtil.getEnum(Category.class, resultSet.getString("category"), Category.GENERAL);
        Warp warp = new Warp(key, constructLocation(resultSet), resultSet.getString("name"));
        warp.setIcon(ItemStack.deserializeBytes(resultSet.getBytes("icon")));
        warp.setCategory(category);
        return warp;
    }

    /**
     * Construct settings from a result set from the database
     *
     * @param resultSet The result set
     * @return The settings
     * @throws SQLException If an error occurs
     */
    public Settings constructSettings(ResultSet resultSet) throws SQLException {

        Biome biome = SkyblockUtil.REGISTRY.getRegistry(RegistryKey.BIOME).get(SkyblockUtil.key(resultSet.getString("biome")));
        if (biome == null) biome = Biome.PLAINS;

        Settings settings = new Settings(resultSet.getInt("key"), resultSet.getString("name"));
        settings.setPublicIsland(resultSet.getBoolean("public"));
        settings.setMobSpawning(resultSet.getBoolean("mobSpawning"));
        settings.setAnimalSpawning(resultSet.getBoolean("animalSpawning"));
        settings.setBiome(biome);
        settings.setBanned(GSON.fromJson(resultSet.getString("bans"), UUIDSerialized.class).result());
        return settings;
    }

    /**
     * Run a task off the main thread to avoid taking up resources and breaking a server
     *
     * @param task The task to run asynchronously.
     */
    public void async(Runnable task) {
        this.rosePlugin.getServer().getScheduler().runTaskAsynchronously(this.rosePlugin, task);
    }

    @Override
    public @NotNull List<Supplier<? extends DataMigration>> getDataMigrations() {
        return List.of(CreateInitialTables::new);
    }

    public Map<Integer, Island> getIslandCache() {
        return islandCache;
    }

    public Map<UUID, Member> getUserCache() {
        return userCache;
    }

}