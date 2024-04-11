package xyz.oribuin.skyblock.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

class EntityListeners(private val plugin: xyz.oribuin.skyblock.SkyblockPlugin) : Listener {

    private val islandManager = this.plugin.getManager<skyblock.manager.IslandManager>()
    private val worldManager = this.plugin.getManager<skyblock.manager.WorldManager>()

    private val islandId = NamespacedKey(plugin, "islandId")

    private val spawnReasons = mutableListOf(
        CreatureSpawnEvent.SpawnReason.NATURAL,
        CreatureSpawnEvent.SpawnReason.ENDER_PEARL,
        CreatureSpawnEvent.SpawnReason.NETHER_PORTAL,
        CreatureSpawnEvent.SpawnReason.RAID,
        CreatureSpawnEvent.SpawnReason.SILVERFISH_BLOCK,
        CreatureSpawnEvent.SpawnReason.PATROL,
        CreatureSpawnEvent.SpawnReason.JOCKEY,
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

        if (!this.isCancelled) {
            this.entity.persistentDataContainer.set(islandId, PersistentDataType.INTEGER, island.key)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun EntityMoveEvent.onMove() {
        if (!worldManager.isIslandWorld(this.entity.world))
            return

        val toIsland = islandManager.getIslandFromLoc(this.to) ?: return
        val cont = this.entity.persistentDataContainer

        if (cont.has(islandId)) {
            val mobIsland = cont.getOrDefault(islandId, PersistentDataType.INTEGER, -1)
            if (toIsland.key != mobIsland) {
                this.isCancelled = true
                entity.remove()
            }
        }

    }


    init {
        this.plugin.server.pluginManager.registerEvents(this, this.plugin)
    }

}