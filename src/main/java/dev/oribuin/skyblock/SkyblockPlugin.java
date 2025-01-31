package dev.oribuin.skyblock;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.skyblock.gui.MenuProvider;
import dev.oribuin.skyblock.hook.PAPIProvider;
import dev.oribuin.skyblock.listener.BlockListeners;
import dev.oribuin.skyblock.listener.EntityListeners;
import dev.oribuin.skyblock.listener.PlayerListeners;
import dev.oribuin.skyblock.manager.CommandManager;
import dev.oribuin.skyblock.manager.DataManager;
import dev.oribuin.skyblock.manager.LocaleManager;
import dev.oribuin.skyblock.manager.MenuManager;
import dev.oribuin.skyblock.manager.WorldManager;
import dev.oribuin.skyblock.world.LayeredChunkGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class SkyblockPlugin extends RosePlugin {

    private static final Set<UUID> bypassing = new HashSet<>();
    private static SkyblockPlugin instance;

    public SkyblockPlugin() {
        super(-1, -1,
                DataManager.class,
                LocaleManager.class,
                CommandManager.class
        );

        instance = this;
    }

    @Override
    public void enable() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        List<String> worldeditPlugins = List.of("WorldEdit", "FastAsyncWorldEdit", "AsyncWorldEdit");
        if (worldeditPlugins.stream().map(pluginManager::getPlugin).allMatch(Objects::isNull)) {
            this.getLogger().severe("You need to install WorldEdit or FastAsyncWorldEdit to use this plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new EntityListeners(this), this);
        pluginManager.registerEvents(new PlayerListeners(this), this);

        new PAPIProvider(this).register();
        new LayeredChunkGenerator();
    }

    @Override
    public void reload() {
        super.reload();

        MenuProvider.reload();
    }

    @Override
    public void disable() {

    }

    @Override
    protected @NotNull List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                WorldManager.class,
                MenuManager.class
        );
    }

    public static SkyblockPlugin get() {
        return instance;
    }

    /**
     * Check if a player is bypassing the island protection
     *
     * @param player The player to check
     * @return If the player is bypassing
     */
    public static boolean isBypassing(Player player) {
        if (player == null) return false;
        if (!player.hasPermission("skyblock.bypass")) return false;

        return bypassing.contains(player.getUniqueId());
    }

    /**
     * Add a player to the bypass list
     *
     * @param player The player to add
     */
    public static void addBypass(Player player) {
        bypassing.add(player.getUniqueId());
    }

    /**
     * Remove a player from the bypass list
     *
     * @param player The player to remove
     */
    public static void removeBypass(Player player) {
        bypassing.remove(player.getUniqueId());
    }

}
