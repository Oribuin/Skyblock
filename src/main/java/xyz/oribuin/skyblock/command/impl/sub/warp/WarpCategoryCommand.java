package xyz.oribuin.skyblock.command.impl.sub.warp

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.RoseSubCommand
import dev.rosewood.rosegarden.command.framework.annotation.Inject
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import xyz.oribuin.skyblock.gui.WarpCategoryGUI
import xyz.oribuin.skyblock.util.asMember
import xyz.oribuin.skyblock.util.getIsland
import xyz.oribuin.skyblock.util.getMenu
import xyz.oribuin.skyblock.util.send

class WarpCategoryCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseSubCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(@Inject context: CommandContext) {
        val member = context.asMember(this.rosePlugin)
        val island = member.getIsland(this.rosePlugin)

        if (island == null) {
            this.rosePlugin.send(context.sender, "no-island")
            return
        }

        this.rosePlugin.getMenu(xyz.oribuin.skyblock.gui.WarpCategoryGUI::class).openMenu(member, island)
    }

    override fun getDefaultName(): String = "category"

    override fun getDescriptionKey(): String = "command-warp-category-description"

    override fun getRequiredPermission(): String = "skyblock..warp.category"

    override fun isPlayerOnly(): Boolean = true


}