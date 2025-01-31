package dev.oribuin.skyblock.command.impl.sub.warp;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import dev.oribuin.skyblock.island.Island;
import dev.oribuin.skyblock.island.member.Member;
import dev.oribuin.skyblock.manager.DataManager;

public class WarpNameCommand extends BaseRoseCommand {

    public WarpNameCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, String name) {
        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        Player player = (Player) context.getSender();

        Member member = manager.getMember(player.getUniqueId());
        Island island = manager.getIsland(member.getIsland());
        if (island == null) {
            player.sendMessage("No Island");
            return;
        }

        if (name.isEmpty() || name.length() > 48) {
            player.sendMessage("Incorrect Name");
            return;
        }

        island.getWarp().setName(name);
        manager.cache(island);

        player.sendMessage("Warp Name set to " + name + "!");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("name")
                .permission("skyblock.warp.name")
                .playerOnly(false)
                .arguments(this.createArgumentsDefinition())
                .build();
    }

    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .optional("name", ArgumentHandlers.STRING)
                .build();
    }

}