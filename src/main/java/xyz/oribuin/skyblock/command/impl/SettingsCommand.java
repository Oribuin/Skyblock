package xyz.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import xyz.oribuin.skyblock.gui.SettingsGUI;
import xyz.oribuin.skyblock.util.asMember;
import xyz.oribuin.skyblock.util.getIsland;
import xyz.oribuin.skyblock.util.getMenu;
import xyz.oribuin.skyblock.util.send;

class SettingsCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(context: CommandContext) {
        val member = context.asMember(this.rosePlugin)
        val island = member.getIsland(this.rosePlugin)

        if (island == null) {
            member.onlinePlayer?.let { this.rosePlugin.send(it, "no-island") }
            return
        }

        this.rosePlugin.getMenu(xyz.oribuin.skyblock.gui.SettingsGUI::class).openMenu(member)
    }

    override fun getDefaultName(): String = "settings"

    override fun getDescriptionKey(): String = "command-settings-description"

    override fun getRequiredPermission(): String = "skyblock.warp.settings"

    override fun isPlayerOnly(): Boolean = true
}