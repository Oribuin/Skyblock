package dev.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import dev.oribuin.skyblock.gui.MenuProvider;
import dev.oribuin.skyblock.gui.impl.CreateGUI;
import dev.oribuin.skyblock.gui.impl.PanelGUI;
import dev.oribuin.skyblock.island.Island;
import dev.oribuin.skyblock.island.member.Member;
import dev.oribuin.skyblock.manager.DataManager;

public class BaseCommand extends BaseRoseCommand {

    public BaseCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        Player player = (Player) context.getSender();
        Member member = manager.getMember(player.getUniqueId());
        Island island = manager.getIsland(member.getIsland());

        if (island == null) {
            MenuProvider.get(CreateGUI.class).open(player);
            return;
        }

        MenuProvider.get(PanelGUI.class).open(player, island);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("island")
                .aliases("is", "sb", "skyblock")
                .permission("skyblock.island")
                .playerOnly(false)
                .arguments(this.createArgumentsDefinition())
                .build();
    }

    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .optionalSub("subcommand",
                        new BiomeCommand(this.rosePlugin),
                        new BorderCommand(this.rosePlugin),
                        new CreateCommand(this.rosePlugin),
                        new InviteCommand(this.rosePlugin),
                        new MemberCommand(this.rosePlugin),
                        new SettingsCommand(this.rosePlugin),
                        new TeleportCommand(this.rosePlugin),
                        new WarpCommand(this.rosePlugin)
                );
    }
}