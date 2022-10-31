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
            this["command-invite-success"] = "You have invited %player% to your island!"


        }
    }
}