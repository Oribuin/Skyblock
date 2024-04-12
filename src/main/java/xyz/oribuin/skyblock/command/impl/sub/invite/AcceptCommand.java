package xyz.oribuin.skyblock.command.impl.sub.invite;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.skyblock.island.Island;
import xyz.oribuin.skyblock.manager.DataManager;

public class AcceptCommand extends BaseRoseCommand {
    public AcceptCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
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

//        island.join(player);
        player.sendMessage("add island join functionality");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("accept")
                .descriptionKey("command-invite-accept-description")
                .permission("skyblock.invite.accept")
                .playerOnly(true)
                .build();
    }

}