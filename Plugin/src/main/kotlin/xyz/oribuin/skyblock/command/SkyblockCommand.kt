package xyz.oribuin.skyblock.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.orilibrary.command.Command
import xyz.oribuin.orilibrary.util.HexUtils.colorify
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.gui.IslandGUI

@Command.Info(
    name = "island",
    aliases = ["is", "sb", "skyblock"],
    usage = "/island",
    description = "The main island command for the plugin.",
    playerOnly = false,
    subCommands = [
        BiomesCommand::class,
        BorderCommand::class,
        CreateCommand::class,
        MembersCommand::class,
        TeleportCommand::class
    ],
    permission = "skyblock.use"
)
class SkyblockCommand(private val plugin: SkyblockPlugin) : Command(plugin) {

    override fun runFunction(sender: CommandSender, label: String, args: Array<String>) {
        if (args.isEmpty() && sender is Player) {
            IslandGUI(plugin).create(sender)
            return
        }

        if (args.isEmpty() && sender !is Player) {
            this.subCommands.stream().filter { sender.hasPermission(it.info.permission) }
                .map { it.info }
                .map { colorify("#a6b2fc&l${it.names[0]} &7- &f${it.usage}") }
                .forEach { sender.sendMessage(it) }
            return
        }
        this.runSubCommands(sender, args, {}) {}
    }

    override fun completeString(sender: CommandSender, label: String, args: Array<String>): MutableList<String> {
        return when (args.size) {
            0, 1 -> this.subCommands
                .stream()
                .filter { sender.hasPermission(it.info.permission) }
                .map { it.info.names[0] }
                .toList()

            else -> mutableListOf()
        }
    }

    init {
        this.register({ }, { })
    }

}