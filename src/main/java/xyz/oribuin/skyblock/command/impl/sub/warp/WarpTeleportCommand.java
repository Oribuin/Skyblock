package xyz.oribuin.skyblock.command.impl.sub.warp;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.skyblock.command.argument.WarpArgument;
import xyz.oribuin.skyblock.island.warp.Warp;

public class WarpTeleportCommand extends BaseRoseCommand {

    public WarpTeleportCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Warp warp) {
        Player player = (Player) context.getSender();
        context.getSender().sendMessage("Teleport to warp: " + warp.getName());

        player.teleport(warp.getLocation());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("teleport")
                .descriptionKey("command-warp-teleport-description")
                .permission("skyblock.warp.teleport")
                .playerOnly(true)
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("warp", new WarpArgument())
                .build();
    }

}

class WarpTeleportCommand(rosePlugin:RosePlugin, parent:RoseCommandWrapper) :

RoseSubCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute (@Inject context:CommandContext, warp:skyblock.island.Warp){
        val member = context.asMember(this.rosePlugin)
        this.rosePlugin.getManager < skyblock.manager.IslandManager > ().warpTeleport(warp, member)
    }

    override fun getDefaultName():String = "teleport"

    override fun getDescriptionKey():String = "command-warp-teleport-description"

    override fun getRequiredPermission():String = "skyblock.warp.teleport"

    override fun isPlayerOnly():Boolean = true


}