package xyz.oribuin.skyblock.command.argument

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.ArgumentParser
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo
import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.Bukkit
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.getManager

class IslandArgument(rosePlugin: RosePlugin) : RoseCommandArgumentHandler<Island>(rosePlugin, Island::class.java) {

    private val islandManager = this.rosePlugin.getManager<IslandManager>()

    override fun handleInternal(argumentInfo: RoseCommandArgumentInfo, argumentParser: ArgumentParser): Island {
        val input = argumentParser.next()

        val exception = HandledArgumentException("argument-handler-island-option", StringPlaceholders.single("input", input))

        return this.islandManager.getIsland(Bukkit.getOfflinePlayerIfCached(input) ?: throw exception) ?: throw exception
    }

    override fun suggestInternal(argumentInfo: RoseCommandArgumentInfo, argumentParser: ArgumentParser): MutableList<String> {
        argumentParser.next()
        return this.islandManager.getIslands()
            .map { it.members.filter { x -> x.role == Member.Role.OWNER } }
            .mapNotNull { it.firstOrNull()?.username }
            .toMutableList()
    }

}