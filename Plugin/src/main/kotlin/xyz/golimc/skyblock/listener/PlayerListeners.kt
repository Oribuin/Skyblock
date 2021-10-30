package xyz.golimc.skyblock.listener

import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.golimc.skyblock.SkyblockPlugin
import xyz.golimc.skyblock.manager.DataManager
import xyz.golimc.skyblock.util.getManager

class PlayerListeners(private val plugin: SkyblockPlugin) : Listener {

    private val data = this.plugin.getManager<DataManager>()

    fun PlayerJoinEvent.onJoin() {
        data.getMember(this.player.uniqueId)

        // we're getting the member again because the value above will still return null.
        plugin.server.scheduler.runTaskLaterAsynchronously(plugin, Runnable {
            val member = data.getMember(this.player.uniqueId)
            data.getIsland(member.island)
        }, 2)
    }

    fun PlayerQuitEvent.onQuit() {
        val member = data.getMember(this.player.uniqueId)
        val island = data.getIsland(member.island) ?: return

        data.saveIsland(island)
    }


    init {
        // Register plugin listeners.
        this.plugin.server.pluginManager.registerEvents(this, this.plugin)
    }

}