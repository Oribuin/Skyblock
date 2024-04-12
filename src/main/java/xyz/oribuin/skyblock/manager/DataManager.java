package xyz.oribuin.skyblock.manager;

import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.skyblock.database.migration.CreateInitialTables;
import xyz.oribuin.skyblock.island.Island;
import xyz.oribuin.skyblock.island.Settings;
import xyz.oribuin.skyblock.island.member.BorderColor;
import xyz.oribuin.skyblock.island.member.Member;
import xyz.oribuin.skyblock.island.member.Role;
import xyz.oribuin.skyblock.island.warp.Category;
import xyz.oribuin.skyblock.island.warp.Warp;
import xyz.oribuin.skyblock.util.SkyblockUtil;
import xyz.oribuin.skyblock.util.serializer.UUIDSerialized;

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
                updateSettings.setString(6, island.getSettings().getBiome().name());
                updateSettings.setString(7, GSON.toJson(new UUIDSerialized(island.getSettings().getBanned())));
                updateSettings.executeUpdate();
            }

            // Update Island warp
            String replaceWarp = "REPLACE INTO " + this.getTablePrefix() + "warps (`key`, `name`, icon, category, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement updateWarp = connection.prepareStatement(replaceWarp)) {
                updateWarp.setInt(1, island.getKey());
                updateWarp.setString(2, island.getWarp().getName());
                updateWarp.setBytes(3, serializeItem(island.getWarp().getIcon()));
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
        warp.setIcon(deserializeItem(resultSet.getBytes("icon")));
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
        Settings settings = new Settings(resultSet.getInt("key"), resultSet.getString("name"));
        settings.setPublicIsland(resultSet.getBoolean("public"));
        settings.setMobSpawning(resultSet.getBoolean("mobSpawning"));
        settings.setAnimalSpawning(resultSet.getBoolean("animalSpawning"));
        settings.setBiome(SkyblockUtil.getEnum(Biome.class, resultSet.getString("biome"), Biome.PLAINS));
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

    /**
     * Serialize an item stack into a byte array
     *
     * @param itemStack The item stack to serialize
     * @return The byte array
     */
    public static byte[] serializeItem(@Nullable ItemStack itemStack) {
        if (itemStack == null)
            return new byte[0];

        byte[] data = new byte[0];
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             BukkitObjectOutputStream oos = new BukkitObjectOutputStream(stream)) {
            oos.writeObject(itemStack);
            data = stream.toByteArray();
        } catch (IOException ignored) {
        }

        return data;
    }

    @Nullable
    public static ItemStack deserializeItem(byte[] data) {
        if (data == null || data.length == 0)
            return null;

        ItemStack itemStack = null;
        try (ByteArrayInputStream stream = new ByteArrayInputStream(data);
             BukkitObjectInputStream ois = new BukkitObjectInputStream(stream)) {
            itemStack = (ItemStack) ois.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
        }

        return itemStack;
    }

    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return List.of(CreateInitialTables.class);
    }

    public Map<Integer, Island> getIslandCache() {
        return islandCache;
    }

    public Map<UUID, Member> getUserCache() {
        return userCache;
    }

}

//    /**
//     * The function for creating a new island in the plugin, separate from the saving item due to island key.
//     *
//     * @param owner The owner of the island
//     * @return The new island.
//     */
//    fun createIsland(owner: UUID): xyz.oribuin.skyblock.island.Island {
//        lateinit var island: xyz.oribuin.skyblock.island.Island
//
//        this.databaseConnector.connect {
//            val insertGroup = "INSERT INTO ${this.tablePrefix}islands (owner) VALUES (?)"
//
//            val islandStatement = it.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS)
//            islandStatement.setString(1, owner.toString())
//
//            islandStatement.executeUpdate()
//            val keys = islandStatement.generatedKeys
//            if (keys.next()) {
//                val key = keys.getInt(1)
//                val newIsland = xyz.oribuin.skyblock.island.Island(
//                    key,
//                    owner,
//                    skyblock.util.getNextIslandLocation(
//                        key,
//                        this.rosePlugin.getManager<skyblock.manager.WorldManager>().overworld,
//                        Setting.ISLAND_SIZE.int
//                    )
//                )
//                val username = Bukkit.getPlayer(owner)?.name ?: "Unknown"
//                newIsland.settings.name = "$username's Island"
//                newIsland.warp.name = "$username's Warp"
//                island = newIsland
//                this.saveIsland(newIsland, it) // save the new island that was created.
//            }
//        }
//
//        return island
//    }
//
//    /**
//     * The function for deleting an island from the plugin.
//     *
//     * @param island The island to delete.
//     */
//    fun deleteIsland(island: xyz.oribuin.skyblock.island.Island) {
//        this.islandCache.remove(island.key)
//        this.islandReports.removeIf { it.island.key == island.key }
//
//        this.async {
//            this.databaseConnector.connect {
//
//                val tableNames = listOf("islands", "settings", "members", "warps", "homes", "reports")
//
//                tableNames.forEach { table ->
//                    val deleteQuery = "DELETE FROM ${this.tablePrefix}$table WHERE key = ?"
//                    val deleteStatement = it.prepareStatement(deleteQuery)
//                    deleteStatement.setInt(1, island.key)
//                    deleteStatement.executeUpdate()
//                }
//            }
//        }
//    }
//
//    fun cacheIsland(island: xyz.oribuin.skyblock.island.Island) {
//        this.islandCache[island.key] = island
//
//        // Check if it has been 10 minutes since the last save.
//        if (island.lastSave + 600000 < System.currentTimeMillis()) {
//            this.async { this.databaseConnector.connect { this.saveIsland(island, it) } }
//        }
//
//    }
//
//    /**
//     * Save an island into the plugin database and cache
//     *
//     * @param island The island being saved
//     */
//    private fun saveIsland(island: xyz.oribuin.skyblock.island.Island, connection: Connection) {
//        island.lastSave = System.currentTimeMillis()
//        this.islandCache[island.key] = island
//
//        val main =
//            connection.prepareStatement("REPLACE INTO ${this.tablePrefix}islands (`key`, owner, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
//        main.setInt(1, island.key)
//        main.setString(2, island.owner.toString())
//        main.setDouble(3, island.center.blockX.toDouble())
//        main.setDouble(4, island.center.blockY.toDouble())
//        main.setDouble(5, island.center.blockZ.toDouble())
//        main.setFloat(6, island.center.yaw)
//        main.setFloat(7, island.center.pitch)
//        main.setString(8, island.center.world?.name)
//        main.executeUpdate()
//
//        // Save the island settings
//        val settings =
//            connection.prepareStatement("REPLACE INTO ${this.tablePrefix}settings (`key`, `name`, `public`, mobSpawning, animalSpawning, biome, bans) VALUES (?, ?, ?, ?, ?, ?, ?)")
//        settings.setInt(1, island.key)
//        settings.setString(2, island.settings.name)
//        settings.setBoolean(3, island.settings.public)
//        settings.setBoolean(4, island.settings.mobSpawning)
//        settings.setBoolean(5, island.settings.animalSpawning)
//        settings.setString(6, island.settings.biome.name)
//        settings.setString(7, gson.toJson(island.settings.banned))
//        settings.executeUpdate()
//
//        // Save the island warps
//        val warps =
//            connection.prepareStatement("REPLACE INTO ${this.tablePrefix}warps (`key`, `name`, icon, visits, votes, category, disabled, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
//        warps.setInt(1, island.key)
//        warps.setString(2, island.warp.name)
//        warps.setBytes(3, this.serialize(island.warp.icon))
//        warps.setInt(4, island.warp.visits)
//        warps.setInt(5, island.warp.votes)
//        warps.setString(6, gson.toJson(island.warp.category))
//        warps.setBoolean(7, island.warp.disabled)
//        warps.setDouble(8, island.warp.location.blockX.toDouble())
//        warps.setDouble(9, island.warp.location.blockY.toDouble())
//        warps.setDouble(10, island.warp.location.blockZ.toDouble())
//        warps.setFloat(11, island.warp.location.yaw)
//        warps.setFloat(12, island.warp.location.pitch)
//        warps.setString(13, island.warp.location.world?.name)
//        warps.executeUpdate()
//
//        if (island.home != island.center) {
//            val homes =
//                connection.prepareStatement("REPLACE INTO ${this.tablePrefix}homes (`key`, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?)")
//            homes.setInt(1, island.key)
//            homes.setDouble(2, island.home.x)
//            homes.setDouble(3, island.home.y)
//            homes.setDouble(4, island.home.z)
//            homes.setFloat(5, island.home.yaw)
//            homes.setFloat(6, island.home.pitch)
//            homes.setString(7, island.home.world?.name)
//            homes.executeUpdate()
//        }
//
//        // This could be pretty bad but who cares.
//        island.members.forEach { member ->
//            val members =
//                connection.prepareStatement("REPLACE INTO ${this.tablePrefix}members (`key`, player, `username`, `role`, border) VALUES (?, ?, ?, ?, ?)")
//            members.setInt(1, island.key)
//            members.setString(2, member.uuid.toString())
//            members.setString(3, member.username)
//            members.setString(4, member.role.name)
//            members.setString(5, member.border.name)
//            members.executeUpdate()
//        }
//    }
//
//    /**
//     * Save a new report to the database.
//     *
//     * @param report The report being saved.
//     */
//    fun saveReport(report: xyz.oribuin.skyblock.island.Report) {
//        this.islandReports.add(report)
//
//        this.async {
//            this.databaseConnector.connect { connection ->
//                val insert =
//                    connection.prepareStatement("INSERT INTO ${this.tablePrefix}reports (reporter, island, reason, date) VALUES (?, ?, ?, ?)")
//                insert.setString(1, report.reporter.toString())
//                insert.setInt(2, report.island.key)
//                insert.setString(3, report.reason)
//                insert.setLong(4, report.date)
//                insert.executeUpdate()
//            }
//        }
//    }
//
//    /**
//     * Save a member into the cache and the database
//     *
//     * @param member The member being saved.
//     */
//    private fun saveMember(member: xyz.oribuin.skyblock.island.member.Member) {
//        this.userCache[member.uuid] = member
//
//        this.async { _ ->
//            this.databaseConnector.connect {
//                val query =
//                    "REPLACE INTO ${this.tablePrefix}members (`key`, player, `username`,`role`, border) VALUES (?, ?, ?, ?, ?)"
//                val statement = it.prepareStatement(query)
//                statement.setInt(1, member.island)
//                statement.setString(2, member.uuid.toString())
//                statement.setString(3, member.username)
//                statement.setString(4, member.role.name)
//                statement.setString(5, member.border.name)
//                statement.executeUpdate()
//            }
//        }
//    }
//
//    /**
//     * Save a member as an extension function
//     */
//    fun xyz.oribuin.skyblock.island.member.Member.save() = saveMember(this)
//
//    /**
//     * Get a member from the user's UUID.
//     *
//     * @param player The player's unique user id
//     * @return The island member.
//     */
//    fun getMember(player: UUID): xyz.oribuin.skyblock.island.member.Member {
//        val member = this.userCache[player]
//        if (member != null)
//            return member
//
//        val newMember = xyz.oribuin.skyblock.island.member.Member(player)
//        this.databaseConnector.connect {
//            val query = "SELECT `key`, `role`, `border` FROM ${this.tablePrefix}members WHERE player = ?"
//            val statement = it.prepareStatement(query)
//            statement.setString(1, player.toString())
//            val result = statement.executeQuery();
//
//            if (result.next()) {
//                newMember.island = result.getInt("key")
//                newMember.role = xyz.oribuin.skyblock.island.member.Member.Role.valueOf(result.getString("role").uppercase())
//                newMember.border = BorderColor.valueOf(result.getString("border").uppercase())
//                this.userCache[player] = newMember
//            }
//        }
//
//        return newMember
//    }
//
//    /**
//     * Get an island from the island key.
//     *
//     * @param key The island key
//     * @return The island with the matching key or no island.
//     */
//    fun getIsland(key: Int): xyz.oribuin.skyblock.island.Island? {
//        if (key == -1)
//            return null;
//
//        val cachedIsland = this.islandCache[key]
//        if (cachedIsland != null)
//            return cachedIsland
//
//        this.async { _ ->
//            this.databaseConnector.connect {
//                val islandQuery = "SELECT owner, x, y, z, world FROM ${this.tablePrefix}islands WHERE key = ?"
//                val islandStatement = it.prepareStatement(islandQuery)
//                islandStatement.setInt(1, key)
//                val islandResult = islandStatement.executeQuery()
//
//                var island: xyz.oribuin.skyblock.island.Island? = null
//                if (islandResult.next()) {
//                    val owner = islandResult.getString("owner")
//                    val world = islandResult.getString("world")
//                    val x = islandResult.getDouble("x")
//                    val y = islandResult.getDouble("y")
//                    val z = islandResult.getDouble("z")
//                    island = xyz.oribuin.skyblock.island.Island(key, UUID.fromString(owner), Location(Bukkit.getWorld(world), x, y, z))
//                }
//
//                if (island == null)
//                    return@connect
//
//                val warpQuery =
//                    "SELECT name, icon, visits, votes, x, y, z, yaw, pitch, world, category FROM ${this.tablePrefix}warps WHERE key = ?"
//                val warpStatement = it.prepareStatement(warpQuery)
//                warpStatement.setInt(1, island.key)
//                val warpResult = warpStatement.executeQuery()
//
//                // Get the island warp if it exists.
//                if (warpResult.next()) {
//                    val warp = skyblock.island.Warp(
//                        island.key,
//                        Location(
//                            Bukkit.getWorld(warpResult.getString("world")),
//                            warpResult.getDouble("x"),
//                            warpResult.getDouble("y"),
//                            warpResult.getDouble("z")
//                        )
//                    )
//                    warp.name = warpResult.getString("name")
//                    warp.icon = this.deserialize(warpResult.getBytes("icon"))
//                    warp.visits = warpResult.getInt("visits")
//                    warp.votes = warpResult.getInt("votes")
//                    warp.category = gson.fromJson(warpResult.getString("category"), skyblock.island.Warp.Category::class.java)
//                    island.warp = warp
//                }
//
//                /**
//                 * This is where we get all the island settings and assign it to the island
//                 * variable, This comment is entirely to section out everything because I feel like
//                 * im gonna die with how cluttered this is.
//                 */
//                val settingsQuery = "SELECT name, public, mobSpawning, animalSpawning, biome, bans FROM ${this.tablePrefix}settings WHERE key = ?"
//                val settingsState = it.prepareStatement(settingsQuery)
//                settingsState.setInt(1, island.key)
//                val settingsResult = settingsState.executeQuery()
//
//                // Get any island settings if they exist.
//                if (settingsResult.next()) {
//                    val settings = _root_ide_package_.xyz.oribuin.skyblock.island.Settings(key)
//                    settings.name = settingsResult.getString("name")
//                    settings.public = settingsResult.getBoolean("public")
//                    settings.mobSpawning = settingsResult.getBoolean("mobSpawning")
//                    settings.animalSpawning = settingsResult.getBoolean("animalSpawning")
//                    settings.biome = skyblock.util.parseEnum(Biome::class, settingsResult.getString("biome") ?: "PLAINS")
//                    settings.banned = gson.fromJson(settingsResult.getString("bans"), _root_ide_package_.xyz.oribuin.skyblock.island.Settings.Banned::class.java)
//                    island.settings = settings
//                }
//
//                val memberQuery = "SELECT * FROM ${this.tablePrefix}members WHERE key = ?"
//                val members = mutableListOf<xyz.oribuin.skyblock.island.member.Member>()
//                val memberState = it.prepareStatement(memberQuery)
//                memberState.setInt(1, island.key)
//                val result = memberState.executeQuery()
//
//                // Get all the members for that island.
//                while (result.next()) {
//                    val player = UUID.fromString(result.getString("player"))
//                    val newMember = xyz.oribuin.skyblock.island.member.Member(player)
//                    newMember.island = island.key
//                    newMember.username = result.getString("username")
//                    newMember.role = xyz.oribuin.skyblock.island.member.Member.Role.valueOf(result.getString("role").uppercase())
//                    newMember.border = BorderColor.valueOf(result.getString("border").uppercase())
//                    this.userCache[player] = newMember
//                    members.add(newMember)
//                }
//
//                island.members = members
//
//                val homesQuery = "SELECT x, y, z, world, yaw, pitch FROM ${this.tablePrefix}homes WHERE key = ?"
//                val homesState = it.prepareStatement(homesQuery)
//                homesState.setInt(1, key)
//                val homesResult = homesState.executeQuery()
//                if (homesResult.next()) {
//                    val world = Bukkit.getWorld(homesResult.getString("world"))
//                    val x = homesResult.getDouble("x")
//                    val y = homesResult.getDouble("y")
//                    val z = homesResult.getDouble("z")
//                    val yaw = homesResult.getFloat("yaw")
//                    val pitch = homesResult.getFloat("pitch")
//                    island.home = Location(world, x, y, z, yaw, pitch)
//                }
//
//                this.islandCache[key] = island
//            }
//        }
//
//        return this.islandCache[key]
//    }
//
//    /**
//     * Run a task asynchronously.
//     *
//     * @param callback The task callback.
//     */
//    private fun async(callback: Consumer<BukkitTask>) {
//        this.rosePlugin.server.scheduler.runTaskAsynchronously(this.rosePlugin, callback)
//    }
//
//    override fun getDataMigrations(): MutableList<Class<out DataMigration>> {
//        return mutableListOf(xyz.oribuin.skyblock.database.migration.CreateInitialTables::class.java)
//    }
//
//    /**
//     * Serialize an itemstack into a byte array.
//     *
//     *  @param itemStack The itemstack to serialize.
//     *  @return The serialized byte array.
//     */
//    private fun serialize(itemStack: ItemStack): ByteArray {
//        val outputStream = ByteArrayOutputStream()
//        val dataOutput = BukkitObjectOutputStream(outputStream)
//        dataOutput.writeObject(itemStack)
//
//        return outputStream.toByteArray()
//    }
//
//    /**
//     * Deserialize an item stack from a byte array.
//     *
//     * @param bytes The byte array.
//     * @return The item stack.
//     */
//    private fun deserialize(bytes: ByteArray): ItemStack {
//        val inputStream = ByteArrayInputStream(bytes)
//        val dataInput = BukkitObjectInputStream(inputStream)
//        return dataInput.readObject() as ItemStack
//    }
//
//}