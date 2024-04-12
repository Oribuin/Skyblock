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

public class SendCommand extends BaseRoseCommand {

    public SendCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target) {
        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        Player player = (Player) context.getSender();

        // TODO: Check if sender = target
        // TODO: check if target has island
        // TODO: Check if request already exists
        // TODO: blah blah blah
        Island island = manager.getIsland(player.getUniqueId());
        if (island == null) {
            player.sendMessage("No Island");
            return;
        }

        island.invite(player, target);
        player.sendMessage("Invite sent to " + target.getName() + "!");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("send")
                .permission("skyblock.invite.send")
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