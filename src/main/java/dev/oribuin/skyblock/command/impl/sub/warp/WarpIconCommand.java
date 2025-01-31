package dev.oribuin.skyblock.command.impl.sub.warp;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.oribuin.skyblock.island.Island;
import dev.oribuin.skyblock.island.member.Member;
import dev.oribuin.skyblock.manager.DataManager;

public class WarpIconCommand extends BaseRoseCommand {

    public WarpIconCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        Player player = (Player) context.getSender();
        Member member = manager.getMember(player.getUniqueId());
        if (!member.hasIsland()) {
            context.getSender().sendMessage("No Island");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand().clone();

        if (item.getType().isAir()) {
            player.sendMessage("Invalid Icon");
            return;
        }

        Island island = manager.getIsland(member.getIsland());
        if (island == null) return;

        island.getWarp().setIcon(item);
        manager.cache(island);

        StringPlaceholders placeholders = StringPlaceholders.builder("setting", "Warp Icon")
                .add("value", item.getType().name())
                .build();

        player.sendMessage("Warp Icon set to " + item.getType().name() + "!");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("icon")
                .permission("skyblock.warp.icon")
                .playerOnly(false)
                .build();
    }

}