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
    names = ["border"],
    permission = "skyblock.border",
    usage = "/island border"

)
class BorderCommand(private val plugin: SkyblockPlugin) : SubCommand() {

    override fun executeArgument(sender: CommandSender, args: Array<String>) {

        // Check if the sender is a player.
        if (sender !is Player) {
            this.plugin.send(sender, "player-only")
            return
        }

        this.plugin.borderGUI.create(sender)
    }

}