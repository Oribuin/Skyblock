package xyz.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.skyblock.gui.MenuProvider;
import xyz.oribuin.skyblock.gui.impl.CreateGUI;
import xyz.oribuin.skyblock.manager.DataManager;

public class CreateCommand extends BaseRoseCommand {

    public CreateCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        Player player = (Player) context.getSender();

        if (manager.getIsland(player.getUniqueId()) != null) {
            player.sendMessage("You already have an island.");
            return;
        }

        // Open the GUI
        MenuProvider.get(CreateGUI.class).open(player);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("create")
                .permission("skyblock.create")
                .playerOnly(false)
                .build();
    }

}