package xyz.oribuin.skyblock.command.command

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommand
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.gui.CreateGUI
import xyz.oribuin.skyblock.util.asMember
import xyz.oribuin.skyblock.util.getMenu
import xyz.oribuin.skyblock.util.send

class CreateCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(context: CommandContext) {
        val player = context.sender as Player

        val member = player.asMember(this.rosePlugin)
        if (member.hasIsland) {
            this.rosePlugin.send(player, "command-create-has-island")
            return
        }

        this.rosePlugin.getMenu(CreateGUI::class).openMenu(member)
    }


    override fun getDefaultName(): String = "create"

    override fun getDescriptionKey(): String = "command-create-description"

    override fun getRequiredPermission(): String = "skyblock.create"
}