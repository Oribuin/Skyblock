package dev.oribuin.skyblock.command.impl.sub.warp;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;

public class WarpSettingsCommand extends BaseRoseCommand {

    public WarpSettingsCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        context.getSender().sendMessage("Open the GUI");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("settings")
                .permission("skyblock.warp.settings")
                .playerOnly(false)
                .build();
    }

}
