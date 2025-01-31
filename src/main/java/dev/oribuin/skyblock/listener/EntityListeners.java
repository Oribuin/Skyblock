package dev.oribuin.skyblock.listener;

import dev.rosewood.rosegarden.RosePlugin;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Chunk;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import dev.oribuin.skyblock.island.Island;
import dev.oribuin.skyblock.manager.DataManager;
import dev.oribuin.skyblock.manager.WorldManager;

import java.util.List;

public class EntityListeners implements Listener {

    private final RosePlugin plugin;
    private final DataManager manager;
    private final WorldManager worldService;

    public EntityListeners(RosePlugin plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getManager(DataManager.class);
        this.worldService = this.plugin.getManager(WorldManager.class);
    }

    private final List<SpawnReason> spawnReasons = List.of(
            SpawnReason.NATURAL,
            SpawnReason.ENDER_PEARL,
            SpawnReason.NETHER_PORTAL,
            SpawnReason.RAID,
            SpawnReason.SILVERFISH_BLOCK,
            SpawnReason.PATROL,
            SpawnReason.JOCKEY
    );


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSpawn(CreatureSpawnEvent event) {
        if (!this.worldService.isIslandWorld(event.getLocation())) return;
        if (!this.spawnReasons.contains(event.getSpawnReason())) return;

        Island island = this.manager.getIsland(event.getLocation());
        if (island == null) return;

        // Disable Animal Spawning if enabled
        if (!island.getSettings().isAnimalSpawning() && event.getEntity() instanceof Animals) {
            event.setCancelled(true);
            return;
        }

        // Disable Monster Spawning if enabled
        if (!island.getSettings().isMobSpawning() && event.getEntity() instanceof Monster) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMove(EntityMoveEvent event) {
        if (!this.worldService.isIslandWorld(event.getTo())) return;

        Chunk fromChunk = event.getFrom().getChunk();
        Chunk toChunk = event.getTo().getChunk();

        // Make sure they swapped chunk locations
        if (fromChunk.getX() == toChunk.getX() && fromChunk.getZ() == toChunk.getZ()) return;

        Island fromIsland = this.manager.getIsland(event.getFrom());
        Island toIsland = this.manager.getIsland(event.getTo());
        if (fromIsland == null || toIsland == null) return;

        if (fromIsland.getKey() != toIsland.getKey()) {
            event.setCancelled(true);
            event.getEntity().remove();
        }

    }

}