package xyz.oribuin.skyblock.command.command.sub.admin

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.RoseSubCommand
import dev.rosewood.rosegarden.command.framework.annotation.Inject
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.send

class AdminDeleteCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseSubCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(@Inject context: CommandContext, island: Island) {

        val manager = this.rosePlugin.getManager<IslandManager>()
        val sender = context.sender

        manager.deleteIsland(island)
        this.rosePlugin.send(sender, "command-admin-delete-success")
    }

    override fun getDefaultName(): String = "delete"

    override fun getDescriptionKey(): String = "command-admin-delete-description"

    override fun getRequiredPermission(): String = "skyblock.admin.delete"


}