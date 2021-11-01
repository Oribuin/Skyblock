package xyz.oribuin.skyblock.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.manager.WorldManager
import xyz.oribuin.skyblock.util.getManager

class PlayerListeners(private val plugin: SkyblockPlugin) : Listener {

    private val data = this.plugin.getManager<DataManager>()
    private val islandManager = this.plugin.getManager<IslandManager>()
    private val worldManager = this.plugin.getManager<WorldManager>()

    @EventHandler
    fun PlayerTeleportEvent.onTeleport() {
        if (!worldManager.isIslandWorld(this.to.world))
            return

        val island = islandManager.getIslandFromLoc(this.to) ?: return
        val member = data.getMember(player.uniqueId)

        islandManager.createBorder(member, island)
    }

    @EventHandler
    fun PlayerJoinEvent.onJoin() {
        data.getMember(this.player.uniqueId)

        // we're getting the member again because the value above will still return null.
        plugin.server.scheduler.runTaskLaterAsynchronously(plugin, Runnable {
            val member = data.getMember(this.player.uniqueId)
            data.getIsland(member.island)
        }, 2)
    }

    @EventHandler
    fun PlayerQuitEvent.onQuit() {
        val member = data.getMember(this.player.uniqueId)
        val island = data.getIsland(member.island) ?: return

        data.saveIsland(island)
    }

//    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
//    fun PlayerBucketEvent.onBucket() {
//        if (!worldManager.isIslandWorld(this.block.world))
//            return
//
//        if (this.player.hasPermission("skyblock.island.bypass"))
//            return
//
//        this.isCancelled = true
//        val island = islandManager.getIslandFromLoc(block.location) ?: return
//
//        if (island.members.map { it.uuid }.contains(player.uniqueId) || island.trusted.contains(player.uniqueId))
//            this.isCancelled = false
//    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun PlayerShearEntityEvent.onShear() {
        if (!worldManager.isIslandWorld(this.entity.world))
            return

        if (this.player.hasPermission("skyblock.island.bypass"))
            return

        this.isCancelled = true
        val island = islandManager.getIslandFromLoc(this.entity.location) ?: return

        if (island.members.map { it.uuid }.contains(player.uniqueId) || island.trusted.contains(player.uniqueId))
            this.isCancelled = false
    }

    init {
        // Register plugin listeners.
        this.plugin.server.pluginManager.registerEvents(this, this.plugin)
    }

}