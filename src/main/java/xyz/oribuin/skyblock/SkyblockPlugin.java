package xyz.oribuin.skyblock;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.plugin.PluginManager;
import xyz.oribuin.skyblock.hook.PAPIProvider;
import xyz.oribuin.skyblock.hook.PlaceholderProvider;
import xyz.oribuin.skyblock.hook.VaultHook;
import xyz.oribuin.skyblock.listener.BlockListeners;
import xyz.oribuin.skyblock.listener.EntityListeners;
import xyz.oribuin.skyblock.listener.PlayerListeners;
import xyz.oribuin.skyblock.manager.CommandManager;
import xyz.oribuin.skyblock.manager.ConfigurationManager;
import xyz.oribuin.skyblock.manager.DataManager;
import xyz.oribuin.skyblock.manager.LocaleManager;
import xyz.oribuin.skyblock.manager.MenuManager;
import xyz.oribuin.skyblock.manager.WorldManager;
import xyz.oribuin.skyblock.world.LayeredChunkGenerator;

import java.util.List;
import java.util.Objects;

public class SkyblockPlugin extends RosePlugin {

    private static SkyblockPlugin instance;

    public SkyblockPlugin() {
        super(-1, -1,
                ConfigurationManager.class,
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
    public void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                WorldManager.class,
                MenuManager.class
        );
    }

    public static SkyblockPlugin get() {
        return instance;
    }

}
