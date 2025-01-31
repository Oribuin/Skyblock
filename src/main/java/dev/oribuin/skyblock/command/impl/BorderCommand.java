package dev.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import dev.oribuin.skyblock.gui.MenuProvider;
import dev.oribuin.skyblock.gui.impl.BorderGUI;

public class BorderCommand extends BaseRoseCommand {

    public BorderCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        MenuProvider.get(BorderGUI.class).open((Player) context.getSender());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("border")
                .permission("skyblock.border")
                .playerOnly(false)
                .build();
    }

}