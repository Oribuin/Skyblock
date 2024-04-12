package xyz.oribuin.skyblock.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.AbstractLocaleManager;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.skyblock.util.PluginUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LocaleManager extends AbstractLocaleManager {

    public LocaleManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    /**
     * Send a message to a CommandSender
     *
     * @param sender       The CommandSender to send the message to
     * @param messageKey   The message key to send
     * @param placeholders The placeholders to apply to the message
     */
    public void sendMessages(CommandSender sender, String messageKey, StringPlaceholders placeholders) {
        String prefix = this.getLocaleMessage("prefix");
        List<Component> messages = this.getLocaleMessages(messageKey, placeholders);

        if (messages.isEmpty()) return;

        for (Component message : messages) {
            this.sendParsedMessage(sender, prefix + message);
        }
    }

    /**
     * Get a string list from a locale key
     *
     * @param messageKey The key to get the message from
     * @return The message
     */
    public List<Component> getLocaleMessages(String messageKey, StringPlaceholders placeholders) {
        return this.getLocaleStringList(messageKey)
                .stream()
                .map(message -> PluginUtil.color(placeholders.apply(message)))
                .collect(Collectors.toList());
    }

    /**
     * Get a list of strings from a locale key
     *
     * @param key The key to get the string list from
     * @return The string list
     */
    public List<String> getLocaleStringList(String key) {
        Object value = this.loadedLocale.getLocaleValues().get(key);
        if (value instanceof String str)
            return List.of(str);

        if (value instanceof List<?> list) {
            List<String> stringList = new ArrayList<>();
            for (Object obj : list) {
                if (obj instanceof String str)
                    stringList.add(str);
            }
            return stringList;
        }

        return List.of();
    }

    /**
     * Send a custom message to a CommandSender with placeholders
     *
     * @param sender       The CommandSender to send the message to
     * @param message      The message to send
     * @param placeholders The placeholders to apply to the message
     */
    public void sendCustomMessage(CommandSender sender, String message, StringPlaceholders placeholders) {
        if (message.isEmpty())
            return;

        if (placeholders == null)
            placeholders = StringPlaceholders.empty();

        this.handleMessage(sender, PluginUtil.color(this.parsePlaceholders(sender, placeholders.apply(message))));
    }

    /**
     * Send a custom message to a CommandSender with placeholders
     *
     * @param sender       The CommandSender to send the message to
     * @param messages     The messages to send
     * @param placeholders The placeholders to apply to the messages
     */
    public void sendCustomMessage(CommandSender sender, List<String> messages, StringPlaceholders placeholders) {
        if (messages.isEmpty())
            return;

        if (placeholders == null)
            placeholders = StringPlaceholders.empty();

        for (String message : messages) {
            this.handleMessage(sender, PluginUtil.color(this.parsePlaceholders(sender, placeholders.apply(message))));
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
    @NotNull
    public Component format(CommandSender sender, String message, StringPlaceholders placeholders) {
        if (message == null || message.isEmpty())
            return Component.text("");

        if (placeholders == null)
            placeholders = StringPlaceholders.empty();

        return PluginUtil.color(this.parsePlaceholders(sender, placeholders.apply(message)));
    }

    /**
     * Format a list of strings with placeholders
     *
     * @param sender       The CommandSender to send the message to
     * @param messages     The messages to send
     * @param placeholders The placeholders to apply to the messages
     * @return The formatted string
     */
    @NotNull
    public List<Component> format(CommandSender sender, List<String> messages, StringPlaceholders placeholders) {
        if (messages.isEmpty())
            return List.of();

        if (placeholders == null)
            placeholders = StringPlaceholders.empty();

        List<Component> formattedMessages = new ArrayList<>();
        for (String message : messages) {
            formattedMessages.add(PluginUtil.color(this.parsePlaceholders(sender, placeholders.apply(message))));
        }
        return formattedMessages;
    }

}