package xyz.oribuin.skyblock.listener

import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Merchant
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.manager.WorldManager
import xyz.oribuin.skyblock.util.getManager

class BlockListeners(private val plugin: SkyblockPlugin) : Listener {

    private val islandManager = this.plugin.getManager<IslandManager>()
    private val worldManager = this.plugin.getManager<WorldManager>()

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