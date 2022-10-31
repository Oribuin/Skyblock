package xyz.oribuin.skyblock.manager

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.config.CommentedFileConfiguration
import dev.rosewood.rosegarden.config.RoseSetting
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.util.getManager

class ConfigurationManager(rosePlugin: RosePlugin) : AbstractConfigurationManager(rosePlugin, Setting::class.java) {

    enum class Setting(private val key: String, private val defaultValue: Any, private vararg val comments: String) : RoseSetting {
        WORLDNAMES_NORMAL("world-names.NORMAL", "islands_normal", "The name for the main island world"),
        WORLDNAMES_NETHER("world-names.NETHER", "islands_nether", "The name for the nether island world"),
        WORLDNAMES_END("world-names.THE_END", "islands_end", "The name for the end island world"),

        // Island Settings
        ISLAND_SIZE("island-size", 150, "The size of the island"),
        MAX_MEMBERS("max-members", 5, "The maximum amount of members per island"),
        NULL_PLACEHOLDER("null-placeholder", "Unknown", "The PAPI placeholder value when the object is null"),

        // Server Spawn
//        SPAWN_X("server-spawn.x", 0.0),
//        SPAWN_Y("server-spawn.y", 64.0),
//        SPAWN_Z("server-spawn.z", 0.0),
//        SPAWN_YAW("server-spawn.yaw", 180f),
//        SPAWN_PITCH("server-spawn.pitch", 0f),
//        SPAWN_WORLD("server-spawn.world", "world"),

        BIOME_PLAINS_ICON("biomes.PLAINS.icon", "GRASS_BLOCK"),
        BIOME_PLAINS_COST("biomes.PLAINS.cost", 1000.0),

        BIOME_BEACH_ICON("biomes.BEACH.icon", "SAND"),
        BIOME_BEACH_COST("biomes.BEACH.cost", 1000.0),

        BIOME_FLOWER_FOREST_ICON("biomes.FLOWER_FOREST.icon", "CORNFLOWER"),
        BIOME_FLOWER_FOREST_COST("biomes.FLOWER_FOREST.cost", 1000.0),

        BIOME_JUNGLE_ICON("biomes.JUNGLE.icon", "JUNGLE_LEAVES"),
        BIOME_JUNGLE_COST("biomes.JUNGLE.cost", 1000.0),

        BIOME_SWAMP_ICON("biomes.SWAMP.icon", "SLIME_BALL"),
        BIOME_SWAMP_COST("biomes.SWAMP.cost", 1000.0);

        private var value: Any? = null

        override fun getKey() = this.key

        override fun getDefaultValue() = this.defaultValue

        override fun getComments() = this.comments

        override fun getCachedValue() = this.value

        override fun setCachedValue(value: Any?) {
            this.value = value
        }

        override fun getBaseConfig(): CommentedFileConfiguration = SkyblockPlugin.instance.getManager<ConfigurationManager>().config

    }

    override fun getHeader(): Array<String> {
        return arrayOf(
            " ___________           ___.   .__                 __    ",
            " /   _____/  | _____.__.\\_ |__ |  |   ____   ____ |  | __",
            " \\_____  \\|  |/ <   |  | | __ \\|  |  /  _ \\_/ ___\\|  |/ /",
            " /        \\    < \\___  | | \\_\\ \\  |_(  <_> )  \\___|    < ",
            "/_______  /__|_ \\/ ____| |___  /____/\\____/ \\___  >__|_ \\",
            "        \\/     \\/\\/          \\/                 \\/     \\/"
        )
    }
}