package xyz.oribuin.skyblock.manager

import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.orilibrary.manager.Manager

class ConfigManager(private val plugin: SkyblockPlugin) : Manager(plugin) {

    override fun enable() {

        var comments = 0

        this.defaultValues().forEach { t, u ->
            if (t.startsWith("#")) {
                comments++
                this.plugin.config.set("_COMMENT_$comments", u.toString())
                return@forEach
            }

            if (this.plugin.config.get(t) == null) {
                this.plugin.config.set(t, u)
            }
        }

        this.plugin.saveConfig()
    }

    /**
     * Set all the default values for the config.
     */
    private fun defaultValues() = object : HashMap<String, Any>() {
        init {
            // MYSQL Configuration
            this["#1"] = "Configure the data saving settings."
            this["mysql.enabled"] = false
            this["mysql.host"] = "localhost"
            this["mysql.port"] = 3306
            this["mysql.dbname"] = "plugins"
            this["mysql.username"] = "username"
            this["mysql.password"] = "password"
            this["mysql.ssl"] = false
            this["mysql.table-name"] = "skyblock"

            // World Names
            this["#2"] = "Configure the world names"
            this["world-names.NORMAL"] = "islands_normal"
            this["world-names.NETHER"] = "islands_nether"
            this["world-names.THE_END"] = "islands_end"
        }
    }
}