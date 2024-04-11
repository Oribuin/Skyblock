package xyz.oribuin.skyblock.command.impl.sub.warp;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import xyz.oribuin.skyblock.util.asMember;
import xyz.oribuin.skyblock.util.getManager;

class WarpUpvoteCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseSubCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(@Inject context: CommandContext, warp: skyblock.island.Warp) {
        val member = context.asMember(this.rosePlugin)
        val manager = this.rosePlugin.getManager<skyblock.manager.IslandManager>()

        manager.upvoteWarp(warp, member)

    }

    override fun getDefaultName(): String = "upvote"

    override fun getDescriptionKey(): String = "command-warp-upvote-description"

    override fun getRequiredPermission(): String = "skyblock..warp.upvote"

    override fun isPlayerOnly(): Boolean = true


}