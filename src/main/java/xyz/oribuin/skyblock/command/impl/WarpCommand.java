package xyz.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;

public class WarpCommand extends BaseRoseCommand {

    public WarpCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        // TODO: Open the Warps GUI
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("warp")
                .descriptionKey("command-warp-description")
                .permission("skyblock.warp")
                .playerOnly(true)
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder().build();
    }

    //     xyz.oribuin.skyblock.command.impl.sub.warp.WarpCategoryCommand::class.java,
    //    xyz.oribuin.skyblock.command.impl.sub.warp.WarpIconCommand::class.java,
    //    xyz.oribuin.skyblock.command.impl.sub.warp.WarpNameCommand::class.java,
    //    xyz.oribuin.skyblock.command.impl.sub.warp.WarpSettingsCommand::class.java,
    //    xyz.oribuin.skyblock.command.impl.sub.warp.WarpTeleportCommand::class.java

}