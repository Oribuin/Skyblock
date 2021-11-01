package xyz.oribuin.skyblock.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.send
import xyz.oribuin.orilibrary.command.SubCommand

@SubCommand.Info(
    names = ["teleport", "go"],
    usage = "/island teleport [player]",
    permission = "skyblock.teleport"
)
class TeleportCommand(private val plugin: SkyblockPlugin) : SubCommand() {

    private val data = this.plugin.getManager<DataManager>()
    private val islandManager = this.plugin.getManager<IslandManager>()

    override fun executeArgument(sender: CommandSender, args: Array<String>) {

        // Check if the sender is a player.
        if (sender !is Player) {
            this.plugin.send(sender, "player-only")
            return
        }

        val member = this.data.getMember(sender.uniqueId)
        val island = data.getIsland(member.island)

        if (island == null) {
            this.plugin.send(sender, "no-island")
            return
        }

        this.islandManager.teleportToIsland(member, island)
    }
}