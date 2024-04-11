package xyz.oribuin.skyblock.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

class BlockListeners(private val plugin: xyz.oribuin.skyblock.SkyblockPlugin) : Listener {

    private val islandManager = this.plugin.getManager<skyblock.manager.IslandManager>()
    private val worldManager = this.plugin.getManager<skyblock.manager.WorldManager>()

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun BlockBreakEvent.onBreak() {
        if (!worldManager.isIslandWorld(this.block.world))
            return

        if (this.player.hasPermission("skyblock.island.bypass"))
            return

        this.isCancelled = true
        val island = islandManager.getIslandFromLoc(block.location) ?: return

        if (island.members.map { it.uuid }.contains(player.uniqueId) || island.trusted.contains(player.uniqueId))
            this.isCancelled = false
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun BlockPlaceEvent.onPlace() {
        if (!worldManager.isIslandWorld(this.block.world))
            return

        if (this.player.hasPermission("skyblock.island.bypass"))
            return

        this.isCancelled = true
        val island = islandManager.getIslandFromLoc(block.location) ?: return

        if (island.members.map { it.uuid }.contains(player.uniqueId) || island.trusted.contains(player.uniqueId))
            this.isCancelled = false
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun SignChangeEvent.onSignChange() {
        if (!worldManager.isIslandWorld(this.block.world))
            return

        if (this.player.hasPermission("skyblock.island.bypass"))
            return

        this.isCancelled = true
        val island = islandManager.getIslandFromLoc(block.location) ?: return

        if (island.members.map { it.uuid }.contains(player.uniqueId) || island.trusted.contains(player.uniqueId))
            this.isCancelled = false
    }

    init {
        this.plugin.server.pluginManager.registerEvents(this, this.plugin)
    }

}