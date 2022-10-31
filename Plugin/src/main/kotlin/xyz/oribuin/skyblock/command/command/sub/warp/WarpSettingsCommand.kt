package xyz.oribuin.skyblock.command.command.sub.warp

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.RoseSubCommand
import dev.rosewood.rosegarden.command.framework.annotation.Inject
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import xyz.oribuin.skyblock.gui.WarpSettingsGUI
import xyz.oribuin.skyblock.util.asMember
import xyz.oribuin.skyblock.util.getMenu

class WarpSettingsCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseSubCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(@Inject context: CommandContext) {
        this.rosePlugin.getMenu(WarpSettingsGUI::class).openMenu(context.asMember(this.rosePlugin))
    }


    override fun getDefaultName(): String = "settings"

    override fun getDescriptionKey(): String = "command-warp-settings-description"

    override fun getRequiredPermission(): String = "skyblock.command.warp.settings"

    override fun isPlayerOnly(): Boolean = true


}
