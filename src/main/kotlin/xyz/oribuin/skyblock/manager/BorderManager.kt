package xyz.oribuin.skyblock.manager

import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder
import net.minecraft.server.v1_16_R3.WorldBorder
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import xyz.oribuin.skyblock.Skyblock
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.library.Manager

class BorderManager(plugin: Skyblock) : Manager(plugin) {
    override fun reload() {
        Bukkit.getScheduler().cancelTask(scheduleBorderTask().taskId)
        scheduleBorderTask()
    }

    override fun disable() {
        Bukkit.getScheduler().cancelTasks(plugin)
    }

    private fun scheduleBorderTask(): BukkitTask {
        return Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            Bukkit.getOnlinePlayers().forEach { player ->
                val islandManager = plugin.getManager(IslandManager::class)

                playBlueBorder(player, islandManager.getIslandOn(player)?: return@Runnable)
            }
        }, 0, 10)
    }

    private fun playBlueBorder(player: Player, island: Island) {
        val worldBorder = WorldBorder()
        worldBorder.world = (island.center.world as CraftWorld).handle

        worldBorder.warningDistance = 0
        worldBorder.warningTime = 0

        worldBorder.size = island.islandRange.toDouble()
        worldBorder.setCenter(island.center.x, island.center.z)

        (player as CraftPlayer).handle.playerConnection.sendPacket(PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE))

    }

}