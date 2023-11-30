package xyz.oribuin.skyblock.listener

import dev.rosewood.rosegarden.RosePlugin
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.Merchant
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.manager.WorldManager
import xyz.oribuin.skyblock.util.asMember
import xyz.oribuin.skyblock.util.cache
import xyz.oribuin.skyblock.util.getIsland
import xyz.oribuin.skyblock.util.getManager

class PlayerListeners(private val rosePlugin: RosePlugin) : Listener {

    private val islandManager = this.rosePlugin.getManager<IslandManager>()
    private val worldManager = this.rosePlugin.getManager<WorldManager>()

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun PlayerTeleportEvent.onTeleport() {
        if (!worldManager.isIslandWorld(this.to.world))
            return

        val island = islandManager.getIslandFromLoc(this.to) ?: return

        islandManager.createBorder(this.player.asMember(rosePlugin), island)
    }

    @EventHandler
    fun PlayerJoinEvent.onJoin() {
        this.player.asMember(rosePlugin)

        // we're getting the member again because the value above will still return null.
        lateinit var member: Member
        rosePlugin.server.scheduler.runTaskLaterAsynchronously(rosePlugin, Runnable {
            member = this.player.asMember(rosePlugin)
            member.getIsland(rosePlugin)
        }, 2)

        rosePlugin.server.scheduler.runTaskLater(rosePlugin, Runnable {
            val island = islandManager.getIslandFromLoc(player.location) ?: return@Runnable
            islandManager.createBorder(member, island)
        }, 3)
    }

    @EventHandler
    fun PlayerQuitEvent.onQuit() {
        this.player.getIsland(rosePlugin)?.cache(rosePlugin)
    }

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun PlayerInteractEvent.onInteract() {
        val block = this.clickedBlock ?: return
        if (!worldManager.isIslandWorld(block.world))
            return

        if (this.player.hasPermission("skyblock.island.bypass"))
            return

        this.isCancelled = true
        val island = islandManager.getIslandFromLoc(block.location) ?: return

        if (island.members.map { it.uuid }.contains(player.uniqueId) || island.trusted.contains(player.uniqueId))
            this.isCancelled = false
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun PlayerInteractEntityEvent.onInteract() {
        if (!worldManager.isIslandWorld(this.rightClicked.world))
            return

        if (this.player.hasPermission("skyblock.island.bypass"))
            return

        if (this.rightClicked is Mob && this.rightClicked is Merchant)
            return

        this.isCancelled = true
        val island = islandManager.getIslandFromLoc(this.rightClicked.location) ?: return

        if (island.members.map { it.uuid }.contains(player.uniqueId) || island.trusted.contains(player.uniqueId))
            this.isCancelled = false
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun EntityDamageByEntityEvent.onEntityAttack() {
        if (this.damager !is Player)
            return

        val player = this.damager as Player
        if (player.hasPermission("skyblock.island.bypass"))
            return

        if (entity is Animals || entity is Hanging || entity is ArmorStand) {
            this.isCancelled = true
            val island = islandManager.getIslandFromLoc(this.entity.location) ?: return

            if (island.members.map { it.uuid }.contains(player.uniqueId) || island.trusted.contains(player.uniqueId))
                this.isCancelled = false
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun PlayerDeathEvent.onDeath() {
        if (!worldManager.isIslandWorld(this.entity.world))
            return

        this.keepInventory = true
        this.keepLevel = true
        @Suppress("deprecation")
        this.deathMessage = null

        islandManager.getIslandFromLoc(this.entity.location)?.let { this.entity.teleport(it.home) }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun PlayerRespawnEvent.onRespawn() {
        if (!worldManager.isIslandWorld(this.player.location.world))
            return

        val island = islandManager.getIslandFromLoc(this.player.location) ?: return
        this.respawnLocation = island.home
    }

}