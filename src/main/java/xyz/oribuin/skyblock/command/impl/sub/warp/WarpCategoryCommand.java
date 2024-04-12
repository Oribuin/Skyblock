package xyz.oribuin.skyblock.command.impl.sub.warp;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.skyblock.island.member.Member;
import xyz.oribuin.skyblock.manager.DataManager;

public class WarpCategoryCommand extends BaseRoseCommand {

    public WarpCategoryCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        Player player = (Player) context.getSender();

        Member member = manager.getMember(player.getUniqueId());
        if (!member.hasIsland()) {
            player.sendMessage("No Island");
            return;
        }

        player.sendMessage("Open the GUI");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("category")
                .descriptionKey("command-warp-category-description")
                .permission("skyblock.warp.category")
                .playerOnly(true)
                .build();
    }

}