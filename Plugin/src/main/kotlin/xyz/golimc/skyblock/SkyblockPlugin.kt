package xyz.golimc.skyblock

import org.bukkit.Bukkit
import xyz.golimc.skyblock.command.SkyblockCommand
import xyz.golimc.skyblock.gui.BorderGUI
import xyz.golimc.skyblock.gui.CreateIslandGUI
import xyz.golimc.skyblock.listener.PlayerListeners
import xyz.golimc.skyblock.manager.*
import xyz.golimc.skyblock.util.getManager
import xyz.oribuin.orilibrary.OriPlugin

class SkyblockPlugin : OriPlugin() {

    lateinit var createIslandGUI: CreateIslandGUI
    lateinit var borderGUI: BorderGUI

    override fun enablePlugin() {

        val worldeditPlugins = listOf("WorldEdit", "FastAsyncWorldEdit", "AsyncWorldEdit")
        if (worldeditPlugins.map { Bukkit.getPluginManager().getPlugin(it) }.isEmpty()) {
            this.logger.severe("You need to install WorldEdit or FastAsyncWorldEdit to use this plugin.")
            this.server.pluginManager.disablePlugin(this)
            return
        }

        // Load Plugin Managers.
        this.getManager<ConfigManager>()
        this.getManager<MessageManager>()
        this.getManager<WorldManager>()
        this.getManager<DataManager>()
        this.getManager<UserManager>()
        this.getManager<IslandManager>()

        // Register Plugin Command.
        SkyblockCommand(this)

        // Register Plugin Listeners
        PlayerListeners(this)

        // Load the plugin GUIs.
        this.createIslandGUI = CreateIslandGUI(this)
        this.borderGUI = BorderGUI(this)

    }

    override fun disablePlugin() {

    }

}
