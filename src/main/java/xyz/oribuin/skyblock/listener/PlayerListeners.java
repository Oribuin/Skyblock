package xyz.oribuin.skyblock.listener;

import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.oribuin.skyblock.island.Island;
import xyz.oribuin.skyblock.island.member.Member;
import xyz.oribuin.skyblock.manager.DataManager;
import xyz.oribuin.skyblock.manager.WorldManager;
import xyz.oribuin.skyblock.util.nms.NMSUtil;

import java.security.SignedObject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerListeners implements Listener {

    private final RosePlugin plugin;
    private final DataManager manager;
    private final WorldManager worldService;

    public PlayerListeners(RosePlugin plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getManager(DataManager.class);
        this.worldService = this.plugin.getManager(WorldManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent event) {
        if (!this.worldService.isIslandWorld(event.getTo())) return;

        Island island = this.manager.getIsland(event.getTo());
        if (island == null) return;

        if (island.isBanned(event.getPlayer()) && !event.getPlayer().hasPermission("skyblock.ban.bypass")) {
            event.setCancelled(true);
            // TODO: Add locale message
            return;
        }

        Member member = this.manager.getMember(event.getPlayer().getUniqueId());
        if (member == null) return;

        NMSUtil.sendWorldBorder(
                event.getPlayer(),
                member.getBorder(),
                island.getSize(),
                island.getCenter().toCenterLocation()
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLogin(PlayerJoinEvent event) {
        CompletableFuture.runAsync(() -> this.manager.loadMember(event.getPlayer().getUniqueId()))
                .thenRun(() -> {
                    Island island = manager.getIsland(event.getPlayer().getLocation());
                    Member member = manager.getMember(event.getPlayer().getUniqueId());
                    if (island == null || member == null) return;

                    NMSUtil.sendWorldBorder(
                            event.getPlayer(),
                            member.getBorder(),
                            island.getSize(),
                            island.getCenter().toCenterLocation()
                    );
                });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        CompletableFuture.runAsync(() -> this.manager.saveMember(uuid)).thenRun(() -> this.manager.getUserCache().remove(uuid));
    }

    // TODO: Add Other Events :3
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onShear(PlayerShearEntityEvent event) {
        if (!this.worldService.isIslandWorld(event.getEntity().getLocation())) return;

        if (event.getPlayer().hasPermission("skyblock.island.bypass")) return;

        event.setCancelled(true);
        Island island = this.manager.getIsland(event.getEntity().getLocation());
        if (island == null) return;

        if (!island.isMember(event.getPlayer()) && !island.isTrusted(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

}

//    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
//    fun PlayerInteractEvent.onInteract() {
//        val block = this.clickedBlock ?: return
//        if (!worldManager.isIslandWorld(block.world))
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
//
//    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
//    fun PlayerInteractEntityEvent.onInteract() {
//        if (!worldManager.isIslandWorld(this.rightClicked.world))
//            return
//
//        if (this.player.hasPermission("skyblock.island.bypass"))
//            return
//
//        if (this.rightClicked is Mob && this.rightClicked is Merchant)
//            return
//
//        this.isCancelled = true
//        val island = islandManager.getIslandFromLoc(this.rightClicked.location) ?: return
//
//        if (island.members.map { it.uuid }.contains(player.uniqueId) || island.trusted.contains(player.uniqueId))
//            this.isCancelled = false
//    }
//
//    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
//    fun EntityDamageByEntityEvent.onEntityAttack() {
//        if (this.damager !is Player)
//            return
//
//        val player = this.damager as Player
//        if (player.hasPermission("skyblock.island.bypass"))
//            return
//
//        if (entity is Animals || entity is Hanging || entity is ArmorStand) {
//            this.isCancelled = true
//            val island = islandManager.getIslandFromLoc(this.entity.location) ?: return
//
//            if (island.members.map { it.uuid }.contains(player.uniqueId) || island.trusted.contains(player.uniqueId))
//                this.isCancelled = false
//        }
//    }
//
//    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
//    fun PlayerDeathEvent.onDeath() {
//        if (!worldManager.isIslandWorld(this.entity.world))
//            return
//
//        this.keepInventory = true
//        this.keepLevel = true
//        @Suppress("deprecation")
//        this.deathMessage = null
//
//        islandManager.getIslandFromLoc(this.entity.location)?.let { this.entity.teleport(it.home) }
//
//    }
//
//    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
//    fun PlayerRespawnEvent.onRespawn() {
//        if (!worldManager.isIslandWorld(this.player.location.world))
//            return
//
//        val island = islandManager.getIslandFromLoc(this.player.location) ?: return
//        this.respawnLocation = island.home
//    }