package xyz.oribuin.skyblock

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.manager.Manager
import xyz.oribuin.skyblock.hook.PAPI
import xyz.oribuin.skyblock.hook.VaultHook
import xyz.oribuin.skyblock.listener.BlockListeners
import xyz.oribuin.skyblock.listener.EntityListeners
import xyz.oribuin.skyblock.listener.PlayerListeners
import xyz.oribuin.skyblock.manager.CommandManager
import xyz.oribuin.skyblock.manager.ConfigurationManager
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.manager.LocaleManager
import xyz.oribuin.skyblock.manager.MenuManager
import xyz.oribuin.skyblock.manager.WorldManager

class SkyblockPlugin : RosePlugin(
    -1,
    -1,
    ConfigurationManager::class.java,
    DataManager::class.java,
    LocaleManager::class.java,
    CommandManager::class.java
) {

    override fun enable() {

        // Check for worldedit.
        val worldeditPlugins = listOf("WorldEdit", "FastAsyncWorldEdit", "AsyncWorldEdit")
        if (worldeditPlugins.map { this.server.pluginManager.getPlugin(it) }.isEmpty()) {
            this.logger.severe("You need to install WorldEdit or FastAsyncWorldEdit to use this plugin.")
            this.server.pluginManager.disablePlugin(this)
            return
        }

        // Check for vault
        if (!this.server.pluginManager.isPluginEnabled("Vault")) {
            this.logger.severe("You need to install Vault to use this plugin.")
            this.server.pluginManager.disablePlugin(this)
            return
        }

        // Register Plugin Listeners
        this.server.pluginManager.registerEvents(BlockListeners(this), this)
        this.server.pluginManager.registerEvents(EntityListeners(this), this)
        this.server.pluginManager.registerEvents(PlayerListeners(this), this)

        // Load Vault Hook
        VaultHook()
        PAPI(this)
    }

    override fun disable() {
        // Unused for now.
    }

    override fun getManagerLoadPriority(): MutableList<Class<out Manager>> {
        return mutableListOf(
            WorldManager::class.java,
            IslandManager::class.java,
            MenuManager::class.java,
        )
    }

    companion object {
        lateinit var instance: SkyblockPlugin
    }

    init {
        instance = this
    }

}
