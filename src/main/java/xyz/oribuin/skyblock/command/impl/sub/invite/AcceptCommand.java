package xyz.oribuin.skyblock.command.impl.sub.invite

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.RoseSubCommand
import dev.rosewood.rosegarden.command.framework.annotation.Inject
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.asMember
import xyz.oribuin.skyblock.util.getManager

class AcceptCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseSubCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(@Inject context: CommandContext) =
        this.rosePlugin.getManager<skyblock.manager.IslandManager>().acceptInvite(context.asMember(this.rosePlugin))

    override fun getDefaultName(): String = "accept"

    override fun isPlayerOnly(): Boolean = true

    override fun getDescriptionKey(): String = "command-invite-accept-description"

    override fun getRequiredPermission(): String = "skyblock..invite.accept"

}