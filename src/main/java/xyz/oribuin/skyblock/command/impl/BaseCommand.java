package xyz.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.skyblock.island.member.Member;
import xyz.oribuin.skyblock.manager.DataManager;

public class BaseCommand extends BaseRoseCommand {

    public BaseCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        Player player = (Player) context.getSender();
        Member member = manager.getMember(player.getUniqueId());

        if (!member.hasIsland()) {
            // TODO: Create new island
            player.sendMessage("create new island wooooooooo");
            return;
        }

        player.sendMessage("open the island menu");

    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("island")
                .descriptionKey("command-island-description")
                .permission("skyblock.island")
                .playerOnly(true)
                .build();
    }

}