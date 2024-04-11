package xyz.oribuin.skyblock.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import xyz.oribuin.skyblock.island.warp.Warp;

import java.util.List;

public class WarpArgument extends ArgumentHandler<Warp> {

    public WarpArgument() {
        super(Warp.class);
    }

    @Override
    public Warp handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        return null;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return null;
    }
}

//import dev.rosewood.rosegarden.RosePlugin
//import dev.rosewood.rosegarden.command.framework.ArgumentParser
//import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler
//import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo
//import dev.rosewood.rosegarden.utils.HexUtils
//import dev.rosewood.rosegarden.utils.StringPlaceholders
//import org.bukkit.ChatColor
//import xyz.oribuin.skyblock.island.warp.Warp
//import xyz.oribuin.skyblock.manager.IslandManager
//import xyz.oribuin.skyblock.util.getManager
//
//@Suppress("deprecation")
//class WarpArgument(rosePlugin: RosePlugin) : RoseCommandArgumentHandler<skyblock.island.Warp>(rosePlugin, skyblock.island.Warp::class.java) {
//
//    private val islandManager = this.rosePlugin.getManager<skyblock.manager.IslandManager>()
//
//    override fun handleInternal(argumentInfo: RoseCommandArgumentInfo, argumentParser: ArgumentParser): skyblock.island.Warp {
//        val input = StringBuilder()
//
//        while (argumentParser.hasNext()) {
//            input.append(argumentParser.next()).append(" ")
//        }
//
//        return islandManager.getWarpsByName(input.toString().lowercase())
//            ?: throw HandledArgumentException("argument-handler-warp-option", StringPlaceholders.of("input", input))
//    }
//
//    override fun suggestInternal(
//        argumentInfo: RoseCommandArgumentInfo,
//        argumentParser: ArgumentParser
//    ): MutableList<String> {
//        argumentParser.next()
//        return this.islandManager.getWarpNames().stream()
//            .map { HexUtils.colorify(it) }
//            .map { ChatColor.stripColor(it) }
//            .distinct()
//            .toList()
//            .filterNotNull()
//            .toMutableList()
//
//    }
//
//}