package xyz.golimc.skyblock.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.golimc.skyblock.SkyblockPlugin
import xyz.golimc.skyblock.manager.DataManager
import xyz.golimc.skyblock.manager.IslandManager
import xyz.golimc.skyblock.util.getManager
import xyz.golimc.skyblock.util.send
import xyz.oribuin.orilibrary.command.SubCommand

@SubCommand.Info(
    names = ["create"],
    permission = "skyblock.create",
    usage = "/island create"

)
class CreateCommand(private val plugin: SkyblockPlugin) : SubCommand() {

    val data = this.plugin.getManager<DataManager>()
    val islandManager = this.plugin.getManager<IslandManager>()

    override fun executeArgument(sender: CommandSender, args: Array<String>) {

        // Check if the sender is a player.
        if (sender !is Player) {
            this.plugin.send(sender, "player-only")
            return
        }

        val member = this.islandManager.getMember(sender.uniqueId)

        if (member.hasIsland) {
            this.plugin.send(sender, "own-island")
            return
        }

    }
}