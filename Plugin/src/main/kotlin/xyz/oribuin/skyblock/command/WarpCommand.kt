package xyz.oribuin.skyblock.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.orilibrary.command.SubCommand
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.send

@SubCommand.Info(
    names = ["warp"],
    usage = "/island warp",
    permission = "skyblock.warp"
)
class WarpCommand(private val plugin: SkyblockPlugin) : SubCommand() {

    private val islandManager = this.plugin.getManager<IslandManager>()
    private val data = this.plugin.getManager<DataManager>()

    override fun executeArgument(sender: CommandSender, args: Array<String>) {

        // Check if the sender is a player
        if (sender !is Player) {
            this.plugin.send(sender, "player-only")
            return
        }

        val member = this.data.getMember(sender.uniqueId)
    }

}