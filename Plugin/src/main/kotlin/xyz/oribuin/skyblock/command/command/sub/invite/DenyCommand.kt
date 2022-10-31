package xyz.oribuin.skyblock.command.command.sub.invite

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.RoseSubCommand
import dev.rosewood.rosegarden.command.framework.annotation.Inject
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.asPlayer
import xyz.oribuin.skyblock.util.getManager

class DenyCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseSubCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(@Inject context: CommandContext) = this.rosePlugin.getManager<IslandManager>().denyInvite(context.asPlayer())

    override fun getDefaultName(): String = "deny"

    override fun isPlayerOnly(): Boolean = true
}
