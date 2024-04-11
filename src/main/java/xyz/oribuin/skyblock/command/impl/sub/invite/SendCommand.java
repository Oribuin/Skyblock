package xyz.oribuin.skyblock.command.impl.sub.invite

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.RoseSubCommand
import dev.rosewood.rosegarden.command.framework.annotation.Inject
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.getManager

class SendCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseSubCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(@Inject context: CommandContext, player: Player) =
        this.rosePlugin.getManager<skyblock.manager.IslandManager>().sendInvite(context.sender as Player, player)

    override fun getDefaultName(): String = "send"

    override fun isPlayerOnly(): Boolean = true

    override fun getRequiredPermission(): String = "skyblock..invite.send"
}
