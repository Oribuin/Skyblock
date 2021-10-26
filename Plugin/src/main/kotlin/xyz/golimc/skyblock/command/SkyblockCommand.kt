package xyz.golimc.skyblock.command

import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.golimc.skyblock.SkyblockPlugin
import xyz.golimc.skyblock.manager.DataManager
import xyz.golimc.skyblock.nms.NMSAdapter
import xyz.oribuin.orilibrary.command.Command
import java.util.function.Consumer

@Command.Info(
    name = "island",
    aliases = ["is", "sb", "skyblock"],
    usage = "/island",
    description = "The main island command for the plugin.",
    playerOnly = false,
    subCommands = [],
    permission = "skyblock.use"
)
class SkyblockCommand(private val plugin: SkyblockPlugin) : Command(plugin) {

    override fun runFunction(sender: CommandSender, label: String, args: Array<String>) {
        val player = sender as Player
        val island = this.plugin.getManager(DataManager::class.java).createIsland(player.uniqueId, player.location.clone().add(0.0, 30.0, 0.0))
        if (island == null) {
            player.sendMessage("island is null")
            return
        }

        island.center.block.type = Material.GLASS
        player.teleport(island.center.clone().add(0.0, 1.0, 0.0))
        NMSAdapter.handler.sendWorldBorder(player, island.ownerMember.border, 200.0, island.center.block.location.clone().add(0.5, 0.0, 0.5))

    }

    override fun completeString(sender: CommandSender, label: String, args: Array<String>): MutableList<String> {
        return mutableListOf()
    }

    init {
        this.register({  }, {  })
    }

}