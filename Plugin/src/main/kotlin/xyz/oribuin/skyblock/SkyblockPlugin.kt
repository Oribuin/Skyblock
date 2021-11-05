package xyz.oribuin.skyblock

import net.milkbowl.vault.economy.Economy
import xyz.oribuin.orilibrary.OriPlugin
import xyz.oribuin.skyblock.command.SkyblockCommand
import xyz.oribuin.skyblock.listener.BlockListeners
import xyz.oribuin.skyblock.listener.PlayerListeners
import xyz.oribuin.skyblock.manager.*
import xyz.oribuin.skyblock.util.getManager

class SkyblockPlugin : OriPlugin() {

    lateinit var vault: Economy

    override fun enablePlugin() {

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

        // Load Vault Eco
        this.vault = this.server.servicesManager.getRegistration(Economy::class.java)?.provider!! // this cant be null so im happy to use !!
    }

    override fun disablePlugin() {

    }

}
