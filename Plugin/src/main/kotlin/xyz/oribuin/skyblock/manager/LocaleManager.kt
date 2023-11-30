package xyz.oribuin.skyblock.manager

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.manager.AbstractLocaleManager
import dev.rosewood.rosegarden.utils.HexUtils
import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.command.CommandSender
import java.util.stream.Collectors

class LocaleManager(rosePlugin: RosePlugin) : AbstractLocaleManager(rosePlugin) {

    /**
     * Send a message to a CommandSender
     *
     * @param sender       The CommandSender to send the message to
     * @param messageKey   The message key to send
     * @param placeholders The placeholders to apply to the message
     */
    fun sendMessages(sender: CommandSender, messageKey: String, placeholders: StringPlaceholders) {
        val prefix = this.getLocaleMessage("prefix")
        val messages = getLocaleMessages(messageKey, placeholders)
        if (messages.isEmpty() || messages.stream().allMatch { it.isEmpty() }) return
        for (message in messages) {
            sendParsedMessage(sender, prefix + message)
        }
    }

    /**
     * Get a string list from a locale key
     *
     * @param messageKey The key to get the message from
     * @return The message
     */
    private fun getLocaleMessages(messageKey: String, placeholders: StringPlaceholders): List<String> {
        return getLocaleStringList(messageKey)
            .stream()
            .map { message -> HexUtils.colorify(placeholders.apply(message)) }
            .collect(Collectors.toList())
    }

    /**
     * Get a list of strings from a locale key
     *
     * @param key The key to get the string list from
     * @return The string list
     */
    private fun getLocaleStringList(key: String): List<String> {
        val value = loadedLocale.localeValues[key]
        if (value is String) return listOf(value)

        if (value is List<*>) {
            val stringList = mutableListOf<String>()
            for (obj in value) {
                if (obj is String) stringList.add(obj)
            }

            return stringList
        }
        return listOf()
    }

    /**
     * Send a custom message to a CommandSender with placeholders
     *
     * @param sender       The CommandSender to send the message to
     * @param message      The message to send
     * @param placeholders The placeholders to apply to the message
     */
    fun sendCustomMessage(sender: CommandSender, message: String, placeholders: StringPlaceholders = StringPlaceholders.empty()) {
        if (message.isEmpty()) return

        handleMessage(sender, HexUtils.colorify(parsePlaceholders(sender, placeholders.apply(message))))
    }

    /**
     * Send a custom message to a CommandSender with placeholders
     *
     * @param sender       The CommandSender to send the message to
     * @param messages     The messages to send
     * @param placeholders The placeholders to apply to the messages
     */
    fun sendCustomMessage(sender: CommandSender, messages: List<String?>, placeholders: StringPlaceholders = StringPlaceholders.empty()) {
        if (messages.isEmpty()) return

        for (message in messages) {
            handleMessage(sender, HexUtils.colorify(parsePlaceholders(sender, placeholders.apply(message))))
        }

    }

    /**
     * Format a string with placeholders
     *
     * @param sender       The CommandSender to send the message to
     * @param message      The message to send
     * @param placeholders The placeholders to apply to the message
     * @return The formatted string
     */
    fun format(sender: CommandSender?, message: String?, placeholders: StringPlaceholders = StringPlaceholders.empty()): String? {
        if (message.isNullOrEmpty()) return null
        return HexUtils.colorify(parsePlaceholders(sender, placeholders.apply(message)))
    }

    /**
     * Format a list of strings with placeholders
     *
     * @param sender       The CommandSender to send the message to
     * @param messages     The messages to send
     * @param placeholders The placeholders to apply to the messages
     * @return The formatted string
     */
    fun format(sender: CommandSender?, messages: List<String>?, placeholders: StringPlaceholders = StringPlaceholders.empty()): List<String> {
        if (messages.isNullOrEmpty()) return listOf()

        val formattedMessages = mutableListOf<String>()
        for (message in messages) {
            formattedMessages.add(HexUtils.colorify(parsePlaceholders(sender, placeholders.apply(message))))
        }

        return formattedMessages
    }

}