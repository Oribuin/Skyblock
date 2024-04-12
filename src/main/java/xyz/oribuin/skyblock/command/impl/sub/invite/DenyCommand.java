package xyz.oribuin.skyblock.command.impl.sub.invite;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;

class DenyCommand(rosePlugin:RosePlugin, parent:RoseCommandWrapper) :

RoseSubCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute (@Inject context:CommandContext) =
    this.rosePlugin.getManager < skyblock.manager.IslandManager > ().denyInvite(context.asPlayer())

    override fun getDefaultName():String = "deny"

    override fun isPlayerOnly():Boolean = true

    override fun getRequiredPermission():String = "skyblock.invite.deny"
}
