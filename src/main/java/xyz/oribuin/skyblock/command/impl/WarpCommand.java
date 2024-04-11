package xyz.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import xyz.oribuin.skyblock.command.impl.sub.warp.WarpCategoryCommand;
import xyz.oribuin.skyblock.command.impl.sub.warp.WarpIconCommand;
import xyz.oribuin.skyblock.command.impl.sub.warp.WarpNameCommand;
import xyz.oribuin.skyblock.command.impl.sub.warp.WarpSettingsCommand;
import xyz.oribuin.skyblock.command.impl.sub.warp.WarpTeleportCommand;
import xyz.oribuin.skyblock.gui.WarpsGUI;
import xyz.oribuin.skyblock.util.asMember;
import xyz.oribuin.skyblock.util.getMenu;

class WarpCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseCommand(
    rosePlugin, parent,
    xyz.oribuin.skyblock.command.impl.sub.warp.WarpCategoryCommand::class.java,
    xyz.oribuin.skyblock.command.impl.sub.warp.WarpIconCommand::class.java,
    xyz.oribuin.skyblock.command.impl.sub.warp.WarpNameCommand::class.java,
    xyz.oribuin.skyblock.command.impl.sub.warp.WarpSettingsCommand::class.java,
    xyz.oribuin.skyblock.command.impl.sub.warp.WarpTeleportCommand::class.java
) {

    @RoseExecutable
    fun execute(context: CommandContext, @Optional command: RoseSubCommand?) {
        this.rosePlugin.getMenu(xyz.oribuin.skyblock.gui.WarpsGUI::class).openMenu(context.asMember(this.rosePlugin))
    }

    override fun getDefaultName(): String = "warp"

    override fun getDescriptionKey(): String = "command-warp-description"

    override fun getRequiredPermission(): String = "skyblock..warp"

    override fun isPlayerOnly(): Boolean = true

}