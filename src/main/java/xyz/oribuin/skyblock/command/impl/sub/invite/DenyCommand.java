package xyz.oribuin.skyblock.command.impl.sub.invite;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.skyblock.island.Island;
import xyz.oribuin.skyblock.manager.DataManager;

public class DenyCommand extends BaseRoseCommand {

    public DenyCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        Player player = (Player) context.getSender();
        // TODO: Check if denyer = target
        // TODO: check if target has island
        // TODO: Check if request  exists
        // TODO: blah blah blah
        Island island = manager.getIsland(player.getUniqueId());
        if (island == null) {
            player.sendMessage("No Island");
            return;
        }

//        island.decline(player);
        player.sendMessage("invite declined functionality not added loser... do that");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("deny")
                .permission("skyblock.invite.deny")
                .playerOnly(false)
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("target", ArgumentHandlers.PLAYER)
                .build();
    }

}