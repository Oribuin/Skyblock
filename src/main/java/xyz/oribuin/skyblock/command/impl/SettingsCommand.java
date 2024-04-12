package xyz.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.skyblock.island.Island;
import xyz.oribuin.skyblock.manager.DataManager;

public class SettingsCommand extends BaseRoseCommand {

    public SettingsCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        Player player = (Player) context.getSender();
        Island island = manager.getIsland(player.getUniqueId());

        if (island == null) {
            // TODO: Locale
            player.sendMessage("No Island");
            return;
        }

        player.sendMessage("imagine the settings gui opened (" + island.getSettings().toString() + ")");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("settings")
                .descriptionKey("command-settings-description")
                .permission("skyblock.warp.settings")
                .playerOnly(false)
                .build();
    }

}