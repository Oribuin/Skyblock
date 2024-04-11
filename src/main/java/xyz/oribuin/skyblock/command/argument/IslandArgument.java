package xyz.oribuin.skyblock.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import xyz.oribuin.skyblock.island.Island;

import java.util.List;

public class IslandArgument extends ArgumentHandler<Island> {

    public IslandArgument() {
        super(Island.class);
    }

    @Override
    public Island handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        // get island by owner name :3
        return null;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        // get island by owner name :3
        return null;
    }

}

//    override fun suggestInternal(
//        argumentInfo: RoseCommandArgumentInfo,
//        argumentParser: ArgumentParser
//    ): MutableList<String> {
//        argumentParser.next()
//        return this.islandManager.getIslands()
//            .map { it.members.filter { x -> x.role == xyz.oribuin.skyblock.island.member.Member.Role.OWNER } }
//            .mapNotNull { it.firstOrNull()?.username }
//            .toMutableList()
//    }