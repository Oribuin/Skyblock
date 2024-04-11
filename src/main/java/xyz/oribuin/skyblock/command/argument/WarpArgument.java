package xyz.oribuin.skyblock.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import xyz.oribuin.skyblock.SkyblockPlugin;
import xyz.oribuin.skyblock.island.Island;
import xyz.oribuin.skyblock.island.warp.Warp;
import xyz.oribuin.skyblock.manager.DataManager;

import java.util.List;
import java.util.stream.Collectors;

public class WarpArgument extends ArgumentHandler<Warp> {

    public WarpArgument() {
        super(Warp.class);
    }

    @Override
    public Warp handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();

        return SkyblockPlugin.get().getManager(DataManager.class)
                .getIslandCache()
                .values()
                .stream()
                .filter(island -> island.getWarp().getName().equalsIgnoreCase(input) && island.getSettings().isPublicIsland())
                .map(Island::getWarp)
                .findFirst()
                .orElseThrow(() -> new HandledArgumentException("argument-handler-warp-option"));
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return SkyblockPlugin.get().getManager(DataManager.class)
                .getIslandCache()
                .values()
                .stream()
                .filter(island -> island.getSettings().isPublicIsland())
                .map(island -> island.getWarp().getName())
                .collect(Collectors.toList());
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