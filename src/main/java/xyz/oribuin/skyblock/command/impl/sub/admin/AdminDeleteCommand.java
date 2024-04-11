package xyz.oribuin.skyblock.command.impl.sub.admin;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.send;

class AdminDeleteCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseSubCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(@Inject context: CommandContext, island: xyz.oribuin.skyblock.island.Island) {

        val manager = this.rosePlugin.getManager<skyblock.manager.IslandManager>()
        val sender = context.sender

        manager.deleteIsland(island)
        this.rosePlugin.send(sender, "command-admin-delete-success")
    }

    override fun getDefaultName(): String = "delete"

    override fun getDescriptionKey(): String = "command-admin-delete-description"

    override fun getRequiredPermission(): String = "skyblock..admin.delete"


}