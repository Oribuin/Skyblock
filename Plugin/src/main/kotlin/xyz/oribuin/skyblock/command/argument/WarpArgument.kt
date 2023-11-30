package xyz.oribuin.skyblock.command.argument

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.ArgumentParser
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo
import dev.rosewood.rosegarden.utils.HexUtils
import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.ChatColor
import xyz.oribuin.skyblock.island.Warp
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.getManager

@Suppress("deprecation")
class WarpArgument(rosePlugin: RosePlugin) : RoseCommandArgumentHandler<Warp>(rosePlugin, Warp::class.java) {

    private val islandManager = this.rosePlugin.getManager<IslandManager>()

    override fun handleInternal(argumentInfo: RoseCommandArgumentInfo, argumentParser: ArgumentParser): Warp {
        val input = StringBuilder()

        while (argumentParser.hasNext()) {
            input.append(argumentParser.next()).append(" ")
        }

        return islandManager.getWarpsByName(input.toString().lowercase())
            ?: throw HandledArgumentException("argument-handler-warp-option", StringPlaceholders.of("input", input))
    }

    override fun suggestInternal(
        argumentInfo: RoseCommandArgumentInfo,
        argumentParser: ArgumentParser
    ): MutableList<String> {
        argumentParser.next()
        return this.islandManager.getWarpNames().stream()
            .map { HexUtils.colorify(it) }
            .map { ChatColor.stripColor(it) }
            .distinct()
            .toList()
            .filterNotNull()
            .toMutableList()

    }

}