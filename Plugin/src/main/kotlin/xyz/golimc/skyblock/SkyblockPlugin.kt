package xyz.golimc.skyblock

import xyz.golimc.skyblock.manager.DataManager
import xyz.oribuin.orilibrary.OriPlugin

class SkyblockPlugin : OriPlugin() {

    override fun enablePlugin() {

        // Load Plugin Managers Async
        this.server.scheduler.runTaskAsynchronously(this, Runnable {
            this.getManager(DataManager::class.java)
        })

    }

    override fun disablePlugin() {

    }

}