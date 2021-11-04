package xyz.oribuin.skyblock

import org.bukkit.Bukkit
import xyz.oribuin.skyblock.command.SkyblockCommand
import xyz.oribuin.skyblock.gui.BorderGUI
import xyz.oribuin.skyblock.gui.CreateIslandGUI
import xyz.oribuin.skyblock.listener.BlockListeners
import xyz.oribuin.skyblock.listener.PlayerListeners
import xyz.oribuin.skyblock.manager.*
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.orilibrary.OriPlugin

class SkyblockPlugin : OriPlugin() {

    private lateinit var createIslandGUI: CreateIslandGUI
    private lateinit var borderGUI: BorderGUI

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
        this.getManager<IslandManager>()
        this.getManager<UserManager>()
        this.getManager<UpgradeManager>()

        // Register Plugin Command.
        SkyblockCommand(this)

        // Register Plugin Listeners
        BlockListeners(this)
        PlayerListeners(this)

        // Load the plugin GUIs.
        this.createIslandGUI = CreateIslandGUI(this)
        this.borderGUI = BorderGUI(this)

    }

    override fun disablePlugin() {

    }

}
