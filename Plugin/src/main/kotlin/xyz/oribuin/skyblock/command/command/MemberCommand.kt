package xyz.oribuin.skyblock.command.command

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommand
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import xyz.oribuin.skyblock.gui.MembersGUI
import xyz.oribuin.skyblock.util.asMember
import xyz.oribuin.skyblock.util.getIsland
import xyz.oribuin.skyblock.util.getMenu
import xyz.oribuin.skyblock.util.send

class MemberCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(context: CommandContext) {
        val member = context.asMember(this.rosePlugin)
        val island = member.getIsland(this.rosePlugin)

        if (island == null) {
            member.onlinePlayer?.let { this.rosePlugin.send(it, "no-island") }
            return
        }

        this.rosePlugin.getMenu(MembersGUI::class).openMenu(member)
    }

    override fun getDefaultName(): String = "members"

    override fun getDescriptionKey(): String = "command-members-description"

    override fun getRequiredPermission(): String = "skyblock.command.members"

    override fun isPlayerOnly(): Boolean = true
}