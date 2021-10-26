package xyz.golimc.skyblock

import org.bukkit.Bukkit
import xyz.golimc.skyblock.command.SkyblockCommand
import xyz.golimc.skyblock.manager.DataManager
import xyz.golimc.skyblock.manager.IslandManager
import xyz.golimc.skyblock.manager.UserManager
import xyz.golimc.skyblock.manager.WorldManager
import xyz.golimc.skyblock.util.getManager
import xyz.oribuin.orilibrary.OriPlugin

class SkyblockPlugin : OriPlugin() {

    override fun enablePlugin() {

        val worldeditPlugins = listOf("WorldEdit", "FastAsyncWorldEdit", "AsyncWorldEdit")
        if (worldeditPlugins.map { Bukkit.getPluginManager().getPlugin(it) }.isEmpty()) {
            this.logger.severe("You need to install WorldEdit/FastAsyncWorldEdit to use this plugin.")
            this.server.pluginManager.disablePlugin(this)
            return
        }

        // Load Plugin Managers
        this.getManager<WorldManager>()
        this.getManager<DataManager>()
        this.getManager<UserManager>()
        this.getManager<IslandManager>()

        // Register Plugin Command
        SkyblockCommand(this)

    }

    override fun disablePlugin() {

    }

}