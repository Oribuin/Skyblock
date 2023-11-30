package xyz.oribuin.skyblock.command.command.sub.warp

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.RoseSubCommand
import dev.rosewood.rosegarden.command.framework.annotation.Inject
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.asMember
import xyz.oribuin.skyblock.util.cache
import xyz.oribuin.skyblock.util.getIsland
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.send

class WarpIconCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseSubCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(@Inject context: CommandContext) {
        val member = context.asMember(this.rosePlugin)
        val island = member.getIsland(this.rosePlugin)

        if (island == null) {
            member.onlinePlayer?.let { this.rosePlugin.send(it, "no-island") }
            return
        }

        val player = context.sender as Player
        val item = player.inventory.itemInMainHand.clone()

        if (item.type.isAir) {
            this.rosePlugin.send(player, "island-warp-icon-invalid")
            return
        }

        island.warp.icon = item
        island.cache(this.rosePlugin)

        val placeholders = StringPlaceholders.builder("setting", "Warp Icon")
            .add("value", name)
            .build()

        this.rosePlugin.getManager<IslandManager>()
            .sendMembersMessage(island, "island-warp-settings-changed", placeholders)
    }

    override fun getDefaultName(): String = "icon"

    override fun getDescriptionKey(): String = "command-warp-icon-description"

    override fun getRequiredPermission(): String = "skyblock.command.warp.icon"

    override fun isPlayerOnly(): Boolean = true


}