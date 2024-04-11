package xyz.oribuin.skyblock.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import xyz.oribuin.skyblock.island.member.Member;

import java.util.List;

public class MemberArgument extends ArgumentHandler<Member> {

    public MemberArgument() {
        super(Member.class);
    }

    @Override
    public Member handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        return null;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return null;
    }

}