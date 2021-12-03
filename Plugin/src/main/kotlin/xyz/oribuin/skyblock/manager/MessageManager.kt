package xyz.oribuin.skyblock.manager

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import xyz.oribuin.orilibrary.manager.Manager
import xyz.oribuin.orilibrary.util.FileUtils
import xyz.oribuin.orilibrary.util.HexUtils
import xyz.oribuin.orilibrary.util.StringPlaceholders
import xyz.oribuin.skyblock.SkyblockPlugin

class MessageManager(private val plugin: SkyblockPlugin) : Manager(plugin) {

    private lateinit var config: FileConfiguration

    override fun enable() {
        val file = FileUtils.createFile(this.plugin, "messages.yml")
        config = YamlConfiguration.loadConfiguration(file)

        var shouldSave = false
        this.defaultValues().filter { config.getString(it.key) == null }.forEach { config.set(it.key, it.value); shouldSave = true }

        // Don't change the config if there's no values missing.
        if (shouldSave)
            config.save(file)

    }

    private fun defaultValues() = object : HashMap<String, String>() {
        init {
            // Plugin Prefix
            this["prefix"] = "#a6b2fc&lSkyblock &8| &f"

            // Island Creation
            this["created-island"] = "You have created your own island!"
            this["own-island"] = "You already own an island! (#a6b2fc/island go&f)"
            this["no-island"] = "You do not have an island, Create one using #a6b2fc/island create&f!"

            // General Island Stuff
            this["changed-biome"] = "Your island biome has been changed to #a6b2fc%biome%&f!"
            this["changed-settings"] = "#a6b2fc%player% &fhas changed your Island #a6b2fc%setting%&f to #a6b2fc%value%&f!"
            this["changed-warp"] = "#a6b2fc%player% &fhas changed your Island Warp #a6b2fc%setting%&f to #a6b2fc%value%&f!"

            // Island Invite
            this["accepted-invite"] = "You have accepted the invite to join an island!"
            this["denied-invite"] = "You have denied the invite to join an island."
            this["no-invite"] = "You have not been invited to an island!"
            this["joined-island"] = "You have joined an island!"
            this["max-members"] = "You have already reached a maximum of 8 island members."
            this["player-has-island"] = "This player already has an island"
            this["cant-invite-self"] = "You cannot invite yourself to your island."
            this["invite-message"] = "You have been invited to %player%'s island! (#a6b2fcClick to accept&f)"
            this["sent-invite"] = "You have sent an invite to %player%"
            this["new-member"] = "#a6b2fc%player% &fhas joined your island!"

            // Island Warp
            this["warp-private"] = "This warp is currently private."

            // Island Bans
            this["is-banned"] = "You are banned from this island."

            // General Island error Messages
            this["invalid-island-role"] = "You do you not have the correct role to do this."

            // Other Stuff?
            this["reload"] = "You have reloaded Skyblock!"

            // Vague error messages
            this["not-enough-money"] = "You do not have enough money to purchase this!"
            this["no-perm"] = "You do not have permission to do this."
            this["invalid-player"] = "Please provide a correct player name."
            this["invalid-args"] = "Please use the correct command usage, %usage%"
            this["unknown-cmd"] = "&fPlease include a valid command."
            this["invalid-block"] = "Please look at a valid block"
            this["player-only"] = "&fOnly a player can execute this command."
            this["console-only"] = "&fOnly console can execute this command."
        }
    }

    /**
     * Send a configuration messageId with placeholders.
     *
     * @param receiver     The CommandSender who receives the messageId.
     * @param messageId    The messageId path
     * @param placeholders The Placeholders
     */
    fun send(receiver: CommandSender, messageId: String, placeholders: StringPlaceholders = StringPlaceholders.empty()) {
        val msg = config.getString(messageId)

        if (msg == null || msg.isEmpty()) {
            receiver.sendMessage(HexUtils.colorify("&c&lError &7| &fThis is a missing message in the messages file, Please contact the server owner about this issue. (Id: $messageId)"))
            return
        }

        val prefix = config.getString("prefix") ?: this.defaultValues()["prefix"]!!
        receiver.sendMessage(HexUtils.colorify(prefix + apply(receiver as Player, placeholders.apply(msg))))
    }

    /**
     * Send a raw message to the receiver with placeholders.
     * Use this to send a message to a player without the message being defined in a config.
     *
     * @param receiver     The message receiver
     * @param message      The message
     * @param placeholders Message Placeholders.
     */
    fun sendRaw(receiver: CommandSender, message: String, placeholders: StringPlaceholders = StringPlaceholders.empty()) {
        receiver.sendMessage(HexUtils.colorify(apply(receiver as Player, placeholders.apply(message))))
    }

    /**
     * Get a message from the messages.yml that are coloured
     *
     * @param message The message id
     * @return The returned message.
     */
    operator fun get(message: String): String {
        return HexUtils.colorify(this.getRaw(message))
    }

    /**
     * Get a message from the messages.yml in raw text.
     *
     * @param message The message id
     * @return The returned message.
     */
    fun getRaw(message: String): String {
        return config.getString(message) ?: this.defaultValues()[message] ?: "Unknown Value: $message"
    }

    private fun apply(sender: CommandSender?, text: String): String {
        return applyPapi(sender, text)
    }
}

fun applyPapi(sender: CommandSender?, text: String): String {
    return if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
        text
    else
        PlaceholderAPI.setPlaceholders(if (sender is Player) sender else null, text)
}

fun apply(sender: OfflinePlayer, text: String): String {
    return if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
        PlaceholderAPI.setPlaceholders(sender, text)
    else
        text;
}