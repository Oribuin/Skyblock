package xyz.oribuin.skyblock.manager

import xyz.oribuin.orilibrary.manager.Manager
import xyz.oribuin.skyblock.SkyblockPlugin

class ConfigManager(private val plugin: SkyblockPlugin) : Manager(plugin) {

    override fun enable() {
        this.defaultValues().forEach { t, u ->
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
            this["mysql.enabled"] = false
            this["mysql.host"] = "localhost"
            this["mysql.port"] = 3306
            this["mysql.dbname"] = "plugins"
            this["mysql.username"] = "username"
            this["mysql.password"] = "password"
            this["mysql.ssl"] = false
            this["mysql.table-name"] = "skyblock"

            // World Names
            this["world-names.NORMAL"] = "islands_normal"
            this["world-names.NETHER"] = "islands_nether"
            this["world-names.THE_END"] = "islands_end"

            // Server spawn because fuck you that's why.
            this["server-spawn.x"] = 0.0
            this["server-spawn.y"] = 64.0
            this["server-spawn.z"] = 0.0
            this["server-spawn.yaw"] = 180f
            this["server-spawn.pitch"] = 0f
            this["server-spawn.world"] = "world"

            // Biome Configurations
            // Plains Biome
//            this["#1"] = "This is where you configure the options available in /island biomes"
            this["biomes.PLAINS.cost"] = 1000.0
            this["biomes.PLAINS.icon"] = "GRASS_BLOCK"
            // Beach Biome
            this["biomes.BEACH.cost"] = 1000.0
            this["biomes.BEACH.icon"] = "SAND"
            // Flower Forest Biome
            this["biomes.FLOWER_FOREST.cost"] = 1000.0
            this["biomes.FLOWER_FOREST.icon"] = "CORNFLOWER"
            // Jungle Biome
            this["biomes.JUNGLE.cost"] = 1000.0
            this["biomes.JUNGLE.icon"] = "JUNGLE_LEAVES"
            // Swamp Biome
            this["biomes.SWAMP.cost"] = 1000.0
            this["biomes.SWAMP.icon"] = "SLIME_BLOCK"

        }
    }
}