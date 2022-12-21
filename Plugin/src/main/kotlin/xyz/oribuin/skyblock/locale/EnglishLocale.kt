package xyz.oribuin.skyblock.locale

import dev.rosewood.rosegarden.locale.Locale

class EnglishLocale : Locale {
    override fun getLocaleName(): String {
        return "en_US"
    }

    override fun getTranslatorName(): String {
        return "Oribuin"
    }

    override fun getDefaultLocaleValues() = object : LinkedHashMap<String, Any>() {
        init {
            // Plugin Prefix
            this["#0"] = "Plugin Command Prefix"
            this["prefix"] = "#a6b2fc&lSkyblock &8| &f"

            // Generic Command Messages
            this["#1"] = "Generic Command Messages"
            this["no-permission"] = "You do not have permission to do that."
            this["only-player"] = "This command can only be executed by a player."
            this["unknown-command"] = "Unknown command, use #a6b2fc&l%cmd% &fhelp for more info"
            this["no-money"] = "You do not have enough money to do that."

            // Help Command
            this["#2"] = "Help Command Messages"
            this["command-help-title"] = "Available Commands:"
            this["command-help-description"] = "Displays the help menu"
            this["command-help-list-description"] = " &8- #a6b2fc/%cmd% %subcmd% %args% &7- &f%desc%"
            this["command-help-list-description-no-args"] = " &8- #a6b2fc/%cmd% %subcmd% &7- &f%desc%"

            // Reload Command
            this["#3"] = "Reload Command Messages"
            this["command-reload-description"] = "Reloads the plugin."
            this["command-reload-reloaded"] = "Configuration and locale files were reloaded"

            // Biomes Command
            this["#4"] = "Biome Command"
            this["command-biome-description"] = "Changes the player's island biome"
            this["command-biome-success"] = "Your island biome has been set to %biome%"

            // Border Command
            this["#5"] = "Border Command"
            this["command-border-description"] = "Changes the player's visible island border"
            this["command-border-success"] = "Your island border has been set to %border%"

            // Create Command
            this["#6"] = "Create Command"
            this["command-create-description"] = "Creates a new island"
            this["command-create-success"] = "You have created a new island!"
            this["command-create-has-island"] = "You already have an island!"

            // Invite Command
            this["#7"] = "Invite Command"
            this["command-invite-description"] = "Invites a player to your island"
            this["command-invite-no-invite"] = "You do not have a pending invite"
            this["command-invite-no-island"] = "You do not have an island"
            this["command-invite-has-island"] = "You already have an island"
            this["command-invite-island-full"] = "Their island has reached the maximum amount of members"

            // Invite Accept Command
            this["#8"] = "Invite Accept Command"
            this["command-invite-accept-description"] = "Accepts an island invite"
            this["command-invite-accept-success"] = "You have joined %island%"
            this["command-invite-accept-joined"] = "%player% has joined your island"

            // Invite Deny Command
            this["#9"] = "Invite Deny Command"
            this["command-invite-deny-description"] = "Denies an island invite"
            this["command-invite-deny-denied"] = "You have denied the invite"
            this["command-invite-deny-other"] = "%player% has denied your invite"

            // Member Command
            this["#10"] = "Member Command"
            this["command-members-description"] = "Manages island members"
            this["command-members-usage"] = "Usage: /is members"

            // Settings Command
            this["#11"] = "Settings Command"
            this["command-settings-description"] = "Manages island settings"
            this["command-settings-usage"] = "Usage: /is settings"

            // Teleport Command
            this["#12"] = "Teleport Command"
            this["command-teleport-description"] = "Teleports to your island"
            this["command-teleport-success"] = "You have been teleported to your island"

            // Warp Commands
            this["#13"] = "Warp Commands"

        }
    }
}