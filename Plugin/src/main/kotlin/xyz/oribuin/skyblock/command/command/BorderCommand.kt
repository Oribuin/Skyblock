package xyz.oribuin.skyblock.command.command

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommand
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.gui.BorderGUI
import xyz.oribuin.skyblock.util.getMenu

class BorderCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(context: CommandContext) = this.rosePlugin.getMenu(BorderGUI::class).openMenu(context.sender as Player)

    override fun getDefaultName(): String = "border"

    override fun getDescriptionKey(): String = "command-border-description"

    override fun getRequiredPermission(): String = "skyblock.command.border"

    override fun isPlayerOnly(): Boolean = true

}