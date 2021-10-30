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
    names = ["border"],
    permission = "skyblock.border",
    usage = "/island border"

)
class BorderCommand(private val plugin: SkyblockPlugin) : SubCommand() {

    private val data = this.plugin.getManager<DataManager>()
    private val islandManager = this.plugin.getManager<IslandManager>()

    override fun executeArgument(sender: CommandSender, args: Array<String>) {

        // Check if the sender is a player.
        if (sender !is Player) {
            this.plugin.send(sender, "player-only")
            return
        }

        this.plugin.borderGUI.create(sender)
    }
}