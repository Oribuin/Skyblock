package xyz.oribuin.skyblock.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.send
import xyz.oribuin.orilibrary.command.SubCommand
import xyz.oribuin.skyblock.gui.BiomesGUI
import xyz.oribuin.skyblock.gui.MembersGUI

@SubCommand.Info(
    names = ["biome"],
    usage = "/island biome",
    permission = "skyblock.biome"
)
class BiomesCommand(private val plugin: SkyblockPlugin) : SubCommand() {

    private val data = this.plugin.getManager<DataManager>()

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

        BiomesGUI(plugin).create(sender, island)
    }
}