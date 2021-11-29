package xyz.oribuin.skyblock.command

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.orilibrary.command.SubCommand
import xyz.oribuin.orilibrary.util.HexUtils.colorify
import xyz.oribuin.orilibrary.util.StringPlaceholders
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.MessageManager
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.send
import java.util.*

@SubCommand.Info(
    names = ["invite"],
    usage = "/is invite <accept/deny/player> [player]",
    permission = "skyblock.invite"
)
class InviteCommand(private val plugin: SkyblockPlugin) : SubCommand() {

    private val data = this.plugin.getManager<DataManager>()

    // requester, requested
    private val inviteMap = mutableMapOf<UUID, UUID>()

    // invite <accept/deny/player> [player]
    // 0       1                     2
    @SuppressWarnings("deprecation")
    override fun executeArgument(sender: CommandSender, args: Array<String>) {

        // Check player.
        if (sender !is Player) {
            this.plugin.send(sender, "player-only")
            return
        }

        if (args.size < 2) {
            this.plugin.send(sender, "invalid-args", StringPlaceholders.single("usage", this.info.usage))
            return
        }

        when (args[1].lowercase()) {
            "accept" -> this.acceptRequest(sender, args)
            "deny" -> this.denyRequest(sender, args)
            else -> {
                val member = this.data.getMember(sender.uniqueId)
                // Check if the member has an island.
                if (!member.hasIsland) {
                    this.plugin.send(sender, "no-island")
                    return
                }

                // Check island members.
                val island = data.getIsland(member.island) ?: return
                if (island.members.size >= 8) {
                    this.plugin.send(sender, "max-members")
                    return
                }

                // Check arguments.
                if (args.size != 2) {
                    this.plugin.send(sender, "invalid-args", StringPlaceholders.single("usage", this.info.usage))
                    return
                }

                // Check player name.
                val target = Bukkit.getPlayer(args[1])
                if (target == null) {
                    this.plugin.send(sender, "invalid-player")
                    return
                }

                // Check if the user is inviting for themself
                if (target.uniqueId == sender.uniqueId) {
                    this.plugin.send(sender, "cant-invite-self")
                    return
                }

                // Check if the target has an island
                val targetAsMember = this.data.getMember(target.uniqueId)
                if (targetAsMember.hasIsland) {
                    this.plugin.send(sender, "player-has-island")
                    return
                }

                val msg = this.plugin.getManager<MessageManager>()
                val inviteMessage = msg.getRaw("invite-message")
                val plc = StringPlaceholders.single("player", sender.name)

                val legacyText = TextComponent.fromLegacyText(colorify(msg.getRaw("prefix") + plc.apply(inviteMessage)))
                    .map {

                        it.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(TextComponent.fromLegacyText(colorify("#a6b2fcClick to accept this request."))))
                        it.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/is invite accept ${sender.name}")
                        it
                    }

                this.inviteMap[sender.uniqueId] = target.uniqueId
                @Suppress("deprecation")
                target.spigot().sendMessage(*legacyText.toTypedArray())
                msg.send(sender, "sent-invite", StringPlaceholders.single("player", target.name))
            }
        }
    }

    /**
     * Accept the island invite request.
     *
     * @param sender The command sender
     * @param args The command arguments.
     */
    private fun acceptRequest(sender: Player, args: Array<String>) {
        val member = this.data.getMember(sender.uniqueId)

        // Check if the member has an island.
        if (member.hasIsland) {
            this.plugin.send(sender, "own-island")
            return
        }

        // check argument size
        if (args.size != 3) {
            this.plugin.send(sender, "invalid-args", StringPlaceholders.single("usage", this.info.usage))
            return
        }

        val player = Bukkit.getPlayer(args[2])
        // Get the player's name
        if (player == null) {
            this.plugin.send(sender, "no-invite")
            return
        }

        // check if the user has a request
        val request = this.inviteMap.filter { it.value == sender.uniqueId && Bukkit.getOfflinePlayer(it.key).uniqueId == player.uniqueId }
            .keys.firstOrNull()

        if (request == null) {
            this.plugin.send(sender, "no-invite")
            return
        }

        val requester = this.data.getMember(request)
        val island = this.data.getIsland(requester.island) ?: return

        island.members.mapNotNull { it.offlinePlayer.player }
            .forEach { this.plugin.send(it, "new-member", StringPlaceholders.single("player", sender.name)) }

        island.members.add(member)
        data.saveIsland(island)
        this.plugin.send(sender, "accepted-invite")
        this.plugin.send(sender, "joined-island")
    }

    /**
     * Deny the island invite request.
     *
     * @param sender The command sender
     * @param args The command arguments.
     */
    private fun denyRequest(sender: Player, args: Array<String>) {
        // check argument size
        if (args.size != 3) {
            this.plugin.send(sender, "invalid-args", StringPlaceholders.single("usage", this.info.usage))
            return
        }

        val player = Bukkit.getPlayer(args[2])
        // Get the player's name
        if (player == null) {
            this.plugin.send(sender, "no-invite")
            return
        }

        // check if the user has a request
        val request = this.inviteMap.filter { it.value == sender.uniqueId && Bukkit.getOfflinePlayer(it.key).uniqueId == player.uniqueId }
            .keys.firstOrNull()

        if (request == null) {
            this.plugin.send(sender, "no-invite")
            return
        }


        this.inviteMap.remove(request)
        this.plugin.send(sender, "denied-invite")
    }

}