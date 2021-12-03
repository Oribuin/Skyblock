package xyz.oribuin.skyblock.listener

import org.bukkit.entity.Animals
import org.bukkit.entity.Monster
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntitySpawnEvent
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.manager.WorldManager
import xyz.oribuin.skyblock.util.getManager

class EntityListeners(private val plugin: SkyblockPlugin) : Listener {

    private val islandManager = this.plugin.getManager<IslandManager>()
    private val worldManager = this.plugin.getManager<WorldManager>()

    private val spawnReasons = mutableListOf(
        CreatureSpawnEvent.SpawnReason.NATURAL,
        CreatureSpawnEvent.SpawnReason.ENDER_PEARL,
        CreatureSpawnEvent.SpawnReason.NETHER_PORTAL,
        CreatureSpawnEvent.SpawnReason.RAID,
        CreatureSpawnEvent.SpawnReason.SILVERFISH_BLOCK,
        CreatureSpawnEvent.SpawnReason.PATROL,
        CreatureSpawnEvent.SpawnReason.JOCKEY
    )

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun CreatureSpawnEvent.onSpawn() {
        if (!worldManager.isIslandWorld(this.entity.world))
            return
        val island = islandManager.getIslandFromLoc(this.location) ?: return

        if (!island.settings.mobSpawning) {
            if (entity is Monster && spawnReasons.contains(this.spawnReason)) {
                this.isCancelled = true
            }
        }

        if (!island.settings.animalSpawning) {
            if (entity is Animals && spawnReasons.contains(this.spawnReason)) {
                this.isCancelled = true
            }
        }
    }


    init {
        this.plugin.server.pluginManager.registerEvents(this, this.plugin)
    }
}