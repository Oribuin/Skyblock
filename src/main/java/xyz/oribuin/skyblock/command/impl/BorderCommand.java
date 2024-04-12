package xyz.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;

public class BorderCommand extends BaseRoseCommand {

    public BorderCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        context.getSender().sendMessage("Open the GUI");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("border")
                .descriptionKey("command-border-description")
                .permission("skyblock.border")
                .playerOnly(true)
                .build();
    }

}