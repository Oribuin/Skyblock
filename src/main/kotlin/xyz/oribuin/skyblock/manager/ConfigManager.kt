package xyz.oribuin.skyblock.manager

import org.bukkit.configuration.file.FileConfiguration
import xyz.oribuin.skyblock.Skyblock
import xyz.oribuin.skyblock.library.Manager
import java.io.File

class ConfigManager(plugin: Skyblock) : Manager(plugin) {
    override fun reload() {
        plugin.reloadConfig()
        plugin.saveDefaultConfig()
        val config = plugin.config

        for (value in Setting.values()) {
            if (config.get(value.key) == null) {
                config.set(value.key, value.defaultValue)
            }
            value.load(config)
        }

        config.save(File(plugin.dataFolder, "config.yml"))
    }

    enum class Setting(val key: String, val defaultValue: Any) {
        WORLD("world-name", "Islands"),
        CMD_ISLAND_CREATE_COOLDOWN("cooldowns.create-island-cooldown", 3600),
        TIME("date-time-format", "HH:mm dd/m/yyyy"),
        SETTINGS_SIZE("island-settings.size", 200.0),
        SETTINGS_KEEP_INVENTORY("island-settings.keep-inventory", true),
        SQL_ENABLED("my-sql.enabled", false),
        SQL_HOSTNAME("my-sql.hostname", ""),
        SQL_PORT("my-sql.port", 3315),
        SQL_DATABASENAME("my-sql.database-name", ""),
        SQL_USERNAME("my-sql.user-name", ""),
        SQL_PASSWORD("my-sql.password", ""),
        SQL_USE_SSL("my-sql.use-ssl", false);


        private var value: Any? = null

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        val boolean: Boolean
            get() = value as Boolean

        /**
         * @return the setting as an int
         */
        val int: Int
            get() = number.toInt()

        /**
         * @return the setting as a long
         */
        val long: Long
            get() = number.toLong()

        /**
         * @return the setting as a double
         */
        val double: Double
            get() = number

        /**
         * @return the setting as a float
         */
        val float: Float
            get() = number.toFloat()

        /**
         * @return the setting as a String
         */
        val string: String
            get() = value as String

        private val number: Double
            get() {
                if (value is Int) {
                    return (value as Int).toDouble()
                } else if (value is Short) {
                    return (value as Short).toDouble()
                } else if (value is Byte) {
                    return (value as Byte).toDouble()
                } else if (value is Float) {
                    return (value as Float).toDouble()
                }
                return value as Double
            }

        /**
         * @return the setting as a string list
         */
        val stringList: List<*>
            get() = value as List<*>

        /**
         * Loads the value from the config and caches it
         */
        fun load(config: FileConfiguration) {
            value = config[key]
        }

    }

    override fun disable() {
        // Unused
    }
}
