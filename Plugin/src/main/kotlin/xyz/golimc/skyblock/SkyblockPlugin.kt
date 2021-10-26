package xyz.golimc.skyblock

import xyz.golimc.skyblock.command.SkyblockCommand
import xyz.golimc.skyblock.manager.DataManager
import xyz.oribuin.orilibrary.OriPlugin

class SkyblockPlugin : OriPlugin() {

    override fun enablePlugin() {

        // Load Plugin Managers Async
        this.server.scheduler.runTaskAsynchronously(this, Runnable {
            this.getManager(DataManager::class.java)
        })

        // Register Plugin Command
        SkyblockCommand(this)

    }

    override fun disablePlugin() {

    }

}