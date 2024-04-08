package xyz.oribuin.skyblock.command.impl

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommand
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.annotation.Optional
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.asMember
import xyz.oribuin.skyblock.util.asPlayer
import xyz.oribuin.skyblock.util.getManager

class TeleportCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(context: CommandContext, @Optional player: Player?) {

        val manager = this.rosePlugin.getManager<skyblock.manager.IslandManager>()
        val target = (player ?: context.asPlayer()).asMember(this.rosePlugin)
        val island = manager.getIsland(target)

        if (island == null) {
            this.rosePlugin.getManager<skyblock.manager.MenuManager>()[xyz.oribuin.skyblock.gui.CreateGUI::class].openMenu(target)
            return
        }

        manager.teleport(target, island.home)

    }

    override fun getDefaultName(): String = "teleport"

    override fun getDefaultAliases(): List<String> = listOf("tp", "home", "go", "spawn")

    override fun getDescriptionKey(): String = "command-teleport-description"

    override fun getRequiredPermission() = "skyblock..teleport"

    override fun isPlayerOnly(): Boolean = true

}