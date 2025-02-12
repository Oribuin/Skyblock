package dev.oribuin.skyblock.command.impl.sub.warp;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import dev.oribuin.skyblock.command.argument.WarpArgument;
import dev.oribuin.skyblock.island.Island;
import dev.oribuin.skyblock.island.warp.Warp;
import dev.oribuin.skyblock.manager.DataManager;

public class WarpTeleportCommand extends BaseRoseCommand {

    public WarpTeleportCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Warp warp) {
        Player player = (Player) context.getSender();

        Island island = this.rosePlugin.getManager(DataManager.class).getIsland(warp.getKey());
        if (island == null) {
            context.getSender().sendMessage("Island not found.");
            return;
        }

        context.getSender().sendMessage("Teleport to warp: " + warp.getName());
        island.teleport(player, warp.getLocation());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("teleport")
                .permission("skyblock.warp.teleport")
                .playerOnly(false)
                .arguments(this.createArgumentsDefinition())
                .build();
    }

    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("warp", new WarpArgument())
                .build();
    }

}