package xyz.golimc.skyblock.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.golimc.skyblock.SkyblockPlugin
import xyz.oribuin.orilibrary.command.Command

@Command.Info(
    name = "island",
    aliases = ["is", "sb", "skyblock"],
    usage = "/island",
    description = "The main island command for the plugin.",
    playerOnly = false,
    subCommands = [CreateCommand::class],
    permission = "skyblock.use"
)
class SkyblockCommand(private val plugin: SkyblockPlugin) : Command(plugin) {

    override fun runFunction(sender: CommandSender, label: String, args: Array<String>) {
        val player = sender as Player

        this.runSubCommands(sender, args, {}) {}
    }

    override fun completeString(sender: CommandSender, label: String, args: Array<String>): MutableList<String> {
        return mutableListOf()
    }

    init {
        this.register({ }, { })
    }

}