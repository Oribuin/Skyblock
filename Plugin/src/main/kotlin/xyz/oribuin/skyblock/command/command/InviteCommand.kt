package xyz.oribuin.skyblock.command.command

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommand
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.RoseSubCommand
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import xyz.oribuin.skyblock.command.command.sub.invite.AcceptCommand
import xyz.oribuin.skyblock.command.command.sub.invite.DenyCommand
import xyz.oribuin.skyblock.command.command.sub.invite.SendCommand

@Suppress("unused")
class InviteCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseCommand(rosePlugin, parent,
    AcceptCommand::class.java,
    DenyCommand::class.java,
    SendCommand::class.java
) {

    @RoseExecutable
    fun execute(context: CommandContext, subcommand: RoseSubCommand) {
        // Unused
    }

    override fun getDefaultName(): String = "invite"

    override fun getDescriptionKey(): String = "command-invite-description"

    override fun getRequiredPermission(): String = "skyblock.command.invite"

    override fun isPlayerOnly(): Boolean = true

}