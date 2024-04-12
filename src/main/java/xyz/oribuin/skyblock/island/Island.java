package xyz.oribuin.skyblock.island;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.skyblock.SkyblockPlugin;
import xyz.oribuin.skyblock.island.warp.Warp;
import xyz.oribuin.skyblock.manager.ConfigurationManager.Setting;
import xyz.oribuin.skyblock.manager.DataManager;
import xyz.oribuin.skyblock.manager.WorldManager;
import xyz.oribuin.skyblock.util.PluginUtil;
import xyz.oribuin.skyblock.world.IslandSchematic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Island {

    public static final NamespacedKey KEY = new NamespacedKey(SkyblockPlugin.get(), "island");
    private final Map<UUID, UUID> invites = new HashMap<>();

    private final int key;
    private final Location center;
    private UUID owner;
    private Location home;
    private Settings settings;
    private Warp warp;
    private List<UUID> members;
    private List<UUID> trusted;
    private int size;
    private boolean dirty;

    /**
     * Create a new island instance with the owner
     *
     * @param key    The island key
     * @param center The center of the island
     * @param owner  The owner of the island
     */
    public Island(int key, Location center, UUID owner) {
        this.key = key;
        this.owner = owner;
        this.center = center;
        this.home = center.clone().add(0, 1, 0);
        this.settings = new Settings(key, Bukkit.getOfflinePlayer(owner).getName() + "'s Island");
        this.warp = new Warp(key, center, this.settings.getIslandName());
        this.members = new ArrayList<>(List.of(owner));
        this.trusted = new ArrayList<>();
        this.size = Setting.ISLAND_SIZE.getInt();
        this.dirty = true;
    }

    /**
     * Create a new island with a randomly assigned location
     *
     * @param key   The island id
     * @param owner The island owner
     */
    public Island(int key, UUID owner) {
        this(key, PluginUtil.getNextIslandLocation(key, SkyblockPlugin
                .get()
                .getManager(WorldManager.class)
                .getWorld(World.Environment.NORMAL)
        ), owner);
    }

    /**
     * Create a brand new island and paste the schematic into the world
     *
     * @param owner     The island owner
     * @param schematic The schematic to paste
     * @param consumer  The result
     */
    public static void create(Player owner, IslandSchematic schematic, Consumer<Island> consumer) {
        DataManager manager = SkyblockPlugin.get().getManager(DataManager.class);
        WorldManager worldManager = SkyblockPlugin.get().getManager(WorldManager.class);
        World islandWorld = worldManager.getWorld(World.Environment.NORMAL);
        if (islandWorld == null) {
            owner.sendMessage("Island world not found.");
            return;
        }

        // Create the island in the database and paste it in
        manager.createIsland(owner, island -> schematic.paste(SkyblockPlugin.get(), island.getCenter(), () -> {
            island.markChunks();
            consumer.accept(island);
            owner.teleportAsync(island.home);
        }));
    }

    /**
     * Teleport the player to the current island, respecting island bans.
     *
     * @param player The player to teleport
     * @return If the player was successfully teleported
     */
    public CompletableFuture<Boolean> teleport(Player player, Location location) {
        // Player is banned from the isladn
        if (this.isBanned(player)) return CompletableFuture.supplyAsync(() -> false);

        // Island is private
        if (!this.settings.isPublicIsland() && !this.isMember(player) && !this.isTrusted(player)) return CompletableFuture.supplyAsync(() -> false);

        // Teleport the player to island home
        return player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Teleport the player to the island home
     *
     * @param player The player to teleport
     * @return If the player was successfully teleported
     */
    public CompletableFuture<Boolean> teleport(Player player) {
        return this.teleport(player, this.home);
    }

    /**
     * Invite a player to the island
     *
     * @param member The player inviting
     * @param target The player being invited
     * @return If the player was successfully invited
     */
    public boolean invite(Player member, Player target) {
        // TODO: Check if player is already on an island
        // TODO: Check if player has been invited already
        // TODO: Add player to invites
        return true;
    }

    /**
     * Remove a player from the island
     *
     * @param target The player being removed
     * @return If the player was successfully removed
     */
    public boolean remove(Player target) {
        // TODO Check if the player is actually on the island
        // TODO Remove the player from the island

        return true;
    }

    /**
     * Trust a player on the island
     *
     * @param member The player trusting
     * @param target The player being trusted
     * @return If the player was successfully trusted
     */
    public boolean trust(Player member, Player target) {
        // TODO Check if the player is actually on the island
        // TODO Check if the player is already trusted
        // TODO: Check if the player is banned
        // TODO Trust the player
        return true;
    }

    /**
     * Untrust a player on the island
     *
     * @param member The player untrusting
     * @param target The player being untrusted
     * @return If the player was successfully untrusted
     */
    public boolean untrust(Player member, Player target) {
        // TODO Check if the player is actually on the island
        // TODO Check if the player is already untrusted
        // TODO Untrust the player


        return true;
    }

    /**
     * Check if a player is trusted on the island
     *
     * @param player The player to check
     * @return If the player is trusted
     */
    public boolean isTrusted(Player player) {
        if (this.members.contains(player.getUniqueId())) return true;

        return this.trusted.contains(player.getUniqueId());
    }

    /**
     * Check if a player is a member of the island
     *
     * @param player The player to check
     * @return If the player is a member
     */
    public boolean isMember(Player player) {
        return this.members.contains(player.getUniqueId());
    }

    /**
     * Check if a player is the owner of the island
     *
     * @param player The player to check
     * @return If the player is the owner
     */

    public boolean isOwner(Player player) {
        return this.owner.equals(player.getUniqueId());
    }

    /**
     * Check if a player is invited to the island
     *
     * @param player The player to check
     * @return If the player is invited
     */
    public boolean isInvited(Player player) {
        return this.invites.containsKey(player.getUniqueId());
    }

    /**
     * Ban a player from entering the island and remove them from the island
     *
     * @param player The player to ban
     * @return If the player was successfully banned
     */
    public boolean ban(Player player) {
        // TODO: Check if player is already banned
        // TODO: Check if player is an island member
        // TODO: Teleport the player off the island if they are on it
        // TODO: If the player is trusted, remove the trust
        return true;
    }

    /**
     * Check if a player is banned from entering the island
     *
     * @param player The player who is trying to enter the island
     * @return If the player can enter
     */
    public boolean isBanned(Player player) {
        return this.settings.getBanned().contains(player.getUniqueId());
    }

    /**
     * Get island chunks within the bounds of the island
     *
     * @return List of chunks
     */
    public List<ChunkPosition> getChunks() {
        List<ChunkPosition> positions = new ArrayList<>();

        Location min = this.getMin();
        Location max = this.getMax();

        int minX = min.getBlockX() >> 4;
        int minZ = min.getBlockZ() >> 4;
        int maxX = max.getBlockX() >> 4;
        int maxZ = max.getBlockZ() >> 4;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                positions.add(new ChunkPosition(this.center.getWorld(), x, z));
            }
        }

        return positions;
    }

    /**
     * Get the minimum bounds of the island
     *
     * @return The minimum bounds
     */
    public Location getMin() {
        return this.center.clone().add((double) -this.size / 2, 0, (double) -this.size / 2);
    }

    /**
     * Get the maximum bounds of the island
     *
     * @return The maximum bounds
     */
    public Location getMax() {
        return this.center.clone().add((double) this.size / 2, 0, (double) this.size / 2);
    }

    /**
     * Check if an island is within the bounds of the island
     *
     * @param location The location to check
     * @return If the location is within the bounds
     */
    public boolean isWithinBounds(Location location) {
        return location.getChunk()
                       .getPersistentDataContainer()
                       .getOrDefault(KEY, PersistentDataType.INTEGER, -1) == this.key;
    }

    /**
     * Marks the chunks within the bounds of the island as belonging to the island
     */
    public void markChunks() {
        this.getChunks().forEach(chunk -> chunk.getChunk().getPersistentDataContainer().set(KEY, PersistentDataType.INTEGER, this.key));
    }

    /**
     * Unmark the chunks within the bounds of the island
     */
    public void unmarkChunks() {
        this.getChunks().forEach(chunk -> chunk.getChunk().getPersistentDataContainer().remove(KEY));
    }

    public int getKey() {
        return key;
    }

    public Location getCenter() {
        return center;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Warp getWarp() {
        return warp;
    }

    public void setWarp(Warp warp) {
        this.warp = warp;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void setMembers(List<UUID> members) {
        this.members = members;
    }

    public List<UUID> getTrusted() {
        return trusted;
    }

    public void setTrusted(List<UUID> trusted) {
        this.trusted = trusted;
    }

    public double getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

}