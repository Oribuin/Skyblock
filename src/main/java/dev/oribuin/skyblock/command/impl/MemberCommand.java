package dev.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import dev.oribuin.skyblock.island.Island;
import dev.oribuin.skyblock.manager.DataManager;

public class MemberCommand extends BaseRoseCommand {

    public MemberCommand(RosePlugin rosePlugin) {
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

        // TODO: Open Menu
        island.getMembers()
                .stream()
                .map(manager::getMember)
                .map(member -> String.format("| %s | %s | %s |", member.getUsername(), member.getRole().name(), member.getBorder().name()))
                .forEach(player::sendMessage);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("members")
                .permission("skyblock.member")
                .playerOnly(false)
                .build();
    }

}