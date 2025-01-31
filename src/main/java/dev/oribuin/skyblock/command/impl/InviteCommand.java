package dev.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.oribuin.skyblock.command.impl.sub.invite.AcceptCommand;
import dev.oribuin.skyblock.command.impl.sub.invite.DenyCommand;
import dev.oribuin.skyblock.command.impl.sub.invite.SendCommand;

public class InviteCommand extends BaseRoseCommand {

    public InviteCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("invite")
                .permission("skyblock.invite")
                .playerOnly(false)
                .arguments(this.createArgumentsDefinition())
                .build();
    }

    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder().requiredSub("command",
                new AcceptCommand(this.rosePlugin),
                new DenyCommand(this.rosePlugin),
                new SendCommand(this.rosePlugin)
        );
    }

}