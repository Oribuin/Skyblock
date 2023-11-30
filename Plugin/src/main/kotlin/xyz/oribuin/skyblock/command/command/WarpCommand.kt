package xyz.oribuin.skyblock.command.command

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommand
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.RoseSubCommand
import dev.rosewood.rosegarden.command.framework.annotation.Optional
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import xyz.oribuin.skyblock.command.command.sub.warp.*
import xyz.oribuin.skyblock.gui.WarpsGUI
import xyz.oribuin.skyblock.util.asMember
import xyz.oribuin.skyblock.util.getMenu

class WarpCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseCommand(
    rosePlugin, parent,
    WarpCategoryCommand::class.java,
    WarpIconCommand::class.java,
    WarpNameCommand::class.java,
    WarpSettingsCommand::class.java,
    WarpTeleportCommand::class.java
) {

    @RoseExecutable
    fun execute(context: CommandContext, @Optional type: RoseSubCommand?) {
        this.rosePlugin.getMenu(WarpsGUI::class).openMenu(context.asMember(this.rosePlugin))
    }

    override fun getDefaultName(): String = "warp"

    override fun getDescriptionKey(): String = "command-warp-description"

    override fun getRequiredPermission(): String = "skyblock.command.warp"

    override fun isPlayerOnly(): Boolean = true

}