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
    names = ["create"],
    permission = "skyblock.create",
    usage = "/island create"

)
class CreateCommand(private val plugin: SkyblockPlugin) : SubCommand() {

    private val data = this.plugin.getManager<DataManager>()
    private val islandManager = this.plugin.getManager<IslandManager>()

    override fun executeArgument(sender: CommandSender, args: Array<String>) {

        // Check if the sender is a player.
        if (sender !is Player) {
            this.plugin.send(sender, "player-only")
            return
        }

        // Check if the player has an island.
        val member = data.getMember(sender.uniqueId)
        if (member.hasIsland) {
            this.plugin.send(sender, "own-island")
            return
        }

        this.plugin.createIslandGUI.create(sender)
    }
}