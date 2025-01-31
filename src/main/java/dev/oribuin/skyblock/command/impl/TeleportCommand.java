package dev.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import dev.oribuin.skyblock.island.Island;
import dev.oribuin.skyblock.manager.DataManager;

public class TeleportCommand extends BaseRoseCommand {

    public TeleportCommand(RosePlugin plugin) {
        super(plugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player player) {
        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        Player target = player != null ? player : (Player) context.getSender();

        Island island = manager.getIsland(target.getUniqueId());

        if (island == null) {
            // TODO: Locale
            context.getSender().sendMessage("No Island");
            return;
        }

        island.teleport((Player) context.getSender()).thenAccept(result -> {
            if (result) {
                context.getSender().sendMessage("Teleporting to Island");
                return;
            }

            context.getSender().sendMessage("Failed to teleport to Island");
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("teleport")
                .aliases("home", "tp", "go")
                .permission("skyblock.teleport")
                .playerOnly(false)
                .arguments(this.createArgumentsDefinition())
                .build();
    }

    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .optional("target", ArgumentHandlers.PLAYER)
                .build();
    }

}