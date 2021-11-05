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
import xyz.oribuin.skyblock.util.parseEnum

class MessageManager(private val plugin: SkyblockPlugin) : Manager(plugin) {

    lateinit var config: FileConfiguration

    override fun enable() {
        val file = FileUtils.createFile(plugin, "messages.yml")
        config = YamlConfiguration.loadConfiguration(file)

        // Set any values that don't exist
        Messages.values().forEach {
            val key = it.name.lowercase().replace("_", "-")
            if (config.get(key) == null)
                config.set(key, it.value)

        }

        config.save(file)
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

        if (msg == null) {
            receiver.sendMessage(HexUtils.colorify("&c&lError &7| &fThis is a missing message in the messages file, Please contact the server owner about this issue. (Id: $messageId)"))
            return
        }

        val prefix = config.getString("prefix") ?: Messages.PREFIX.value
        receiver.sendMessage(HexUtils.colorify(prefix + apply(receiver as? Player, placeholders.apply(msg))))
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
        receiver.sendMessage(HexUtils.colorify(apply(receiver as? Player, placeholders.apply(message))))
    }

    operator fun get(message: String): String {
        return HexUtils.colorify(config.getString(message) ?: parseEnum(Messages::class, message.replace("-", "-")).value)
    }

    override fun disable() {

    }

    private fun apply(sender: CommandSender?, text: String): String {
        return applyPapi(sender, text)
    }

    enum class Messages(val value: String) {
        PREFIX("#a6b2fc&lSkyblock &8| &f"),
        OWN_ISLAND("You already own an island! (#a6b2fc/island go&f)"),
        NO_ISLAND("You do not have an island, Create one using #a6b2fc/island create&f!"),
        CHANGED_BIOME("Your island biome has been changed to #a6b2fc%biome%&f!"),

        RELOAD("You have reloaded Skyblock!"),
        NOT_ENOUGH_MONEY("You do not have enough money to purchase this!"),
        DISABLED_WORLD("You cannot do this in this world."),
        NO_PERM("You do not have permission to do this."),
        INVALID_PLAYER("Please provide a correct player name."),
        INVALID_ARGS("Please use the correct command usage, %usage%"),
        INVALID_AMOUNT("&fPlease provide a valid number."),
        UNKNOWN_CMD("&fPlease include a valid command."),
        INVALID_CRATE("Please provide a valid crate name."),
        INVALID_BLOCK("Please look at a valid block"),
        PLAYER_ONLY("&fOnly a player can execute this command."),
        CONSOLE_ONLY("&fOnly console can execute this command.");
    }

    companion object {
        fun applyPapi(sender: CommandSender?, text: String): String {
            return if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) text else PlaceholderAPI.setPlaceholders(if (sender is Player) sender else null, text)
        }

        fun apply(sender: OfflinePlayer, text: String): String {
            return if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
                PlaceholderAPI.setPlaceholders(sender, text)
            else
                text;
        }
    }
}