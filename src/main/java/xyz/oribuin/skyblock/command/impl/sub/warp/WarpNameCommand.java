package xyz.oribuin.skyblock.command.impl.sub.warp

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.RoseSubCommand
import dev.rosewood.rosegarden.command.framework.annotation.Inject
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import dev.rosewood.rosegarden.utils.StringPlaceholders
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.asMember
import xyz.oribuin.skyblock.util.cache
import xyz.oribuin.skyblock.util.getIsland
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.send

class WarpNameCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseSubCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(@Inject context: CommandContext, name: String) {
        val member = context.asMember(this.rosePlugin)
        val island = member.getIsland(this.rosePlugin)

        if (island == null) {
            member.onlinePlayer?.let { this.rosePlugin.send(it, "no-island") }
            return
        }

        if (name.isEmpty() || name.length > 48) {
            this.rosePlugin.send(context.sender, "command-warp-name-incorrect")
            return
        }

        island.warp.name = name
        island.cache(this.rosePlugin)

        val placeholders = StringPlaceholders.builder("setting", "Warp Name")
            .add("value", name)
            .build()

        this.rosePlugin.getManager<skyblock.manager.IslandManager>()
            .sendMembersMessage(island, "island-warp-settings-changed", placeholders)
    }

    override fun getDefaultName(): String = "name"

    override fun getDescriptionKey(): String = "command-warp-name-description"

    override fun getRequiredPermission(): String = "skyblock..warp.name"

    override fun isPlayerOnly(): Boolean = true


}