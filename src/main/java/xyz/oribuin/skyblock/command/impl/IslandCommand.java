package xyz.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.command.BaseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import xyz.oribuin.skyblock.util.asMember;
import xyz.oribuin.skyblock.util.getManager;

class IslandCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : BaseCommand(rosePlugin, parent) {

    @RoseExecutable
    override fun execute(context: CommandContext) {
        val member = context.asMember(this.rosePlugin)
        val manager = this.rosePlugin.getManager<skyblock.manager.MenuManager>()

        if (!member.hasIsland) {
            manager[xyz.oribuin.skyblock.gui.CreateGUI::class].openMenu(member)
            return
        }

        manager[xyz.oribuin.skyblock.gui.PanelGUI::class].openMenu(member)
    }

    override fun getRequiredPermission(): String = "skyblock..use"

    override fun isPlayerOnly(): Boolean = true

}