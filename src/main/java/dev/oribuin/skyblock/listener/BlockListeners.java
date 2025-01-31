package dev.oribuin.skyblock.listener;

import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import dev.oribuin.skyblock.SkyblockPlugin;
import dev.oribuin.skyblock.island.Island;
import dev.oribuin.skyblock.manager.DataManager;
import dev.oribuin.skyblock.manager.WorldManager;

public class BlockListeners implements Listener {

    private final RosePlugin plugin;
    private final DataManager manager;
    private final WorldManager worldService;

    public BlockListeners(RosePlugin plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getManager(DataManager.class);
        this.worldService = this.plugin.getManager(WorldManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent event) {
        if (!this.worldService.isIslandWorld(event.getBlock().getLocation())) return;
        if (SkyblockPlugin.isBypassing(event.getPlayer())) return;

        Island island = this.manager.getIsland(event.getBlock().getLocation());
        if (island == null) return;

        if (island.isMember(event.getPlayer()) || island.isTrusted(event.getPlayer()))
            return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlace(BlockPlaceEvent event) {
        if (!this.worldService.isIslandWorld(event.getBlock().getLocation())) return;
        if (SkyblockPlugin.isBypassing(event.getPlayer())) return;

        Island island = this.manager.getIsland(event.getBlock().getLocation());
        if (island == null) return;

        if (island.isMember(event.getPlayer()) || island.isTrusted(event.getPlayer()))
            return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onSignChange(SignChangeEvent event) {
        if (!this.worldService.isIslandWorld(event.getBlock().getLocation())) return;
        if (SkyblockPlugin.isBypassing(event.getPlayer())) return;

        Island island = this.manager.getIsland(event.getBlock().getLocation());
        if (island == null) return;

        if (island.isMember(event.getPlayer()) || island.isTrusted(event.getPlayer()))
            return;

        event.setCancelled(true);
    }

}