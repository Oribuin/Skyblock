package xyz.golimc.skyblock.manager

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import org.bukkit.*
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import xyz.golimc.skyblock.SkyblockPlugin
import xyz.golimc.skyblock.util.parseEnum
import xyz.golimc.skyblock.world.IslandSchematic
import xyz.golimc.skyblock.world.VoidGenerator
import xyz.oribuin.orilibrary.manager.Manager
import xyz.oribuin.orilibrary.util.FileUtils
import java.io.File

class WorldManager(private val plugin: SkyblockPlugin) : Manager(plugin) {

    private val worlds = mutableMapOf<World.Environment, World>()
    val schematics = mutableMapOf<String, IslandSchematic>()
    private lateinit var schemConfig: FileConfiguration

    override fun enable() {
        val section = this.plugin.config.getConfigurationSection("world-names")

        // Check if the world names are defined.
        if (section == null || section.getKeys(false).isEmpty()) {
            this.plugin.logger.severe("World Names weren't defined, cannot continue.")
            this.plugin.disablePlugin()
            return
        }

        section.getKeys(false).forEach {
            // Match the island name
            val enviroment = World.Environment.values().find { env -> env.name.equals(it, true) } ?: return
            // Create the worlds.
            val world = this.createWorld(section.getString(it) ?: ("islands_" + enviroment.name.lowercase()), enviroment)
            this.worlds[enviroment] = world
        }

        val schemFolder = File(this.plugin.dataFolder, "schematics")
        var schemFile = File(this.plugin.dataFolder, "schematics.yml")

        if (!schemFile.exists())
            schemFile = FileUtils.createFile(this.plugin, "schematics.yml")

        this.schemConfig = YamlConfiguration.loadConfiguration(schemFile)

        if (!schemFolder.exists())
            schemFolder.mkdir()

        val schemFiles = schemFolder.listFiles() ?: error("Schematics folder does not exist.")
        schemConfig.getKeys(false).forEach { key ->
            val keyFile = schemFiles.find { it.nameWithoutExtension.equals(key, true) }

            if (keyFile == null) {
                this.plugin.logger.severe("Could not fine the $key schematic in the schematics folder.")
                return@forEach
            }

            println("Found Key: $key")
            if (ClipboardFormats.findByFile(keyFile) != null) {

                val schemSection = schemConfig.getConfigurationSection(key) ?: error(key)

                schemSection.getKeys(false).map { it }.forEach { println(it) }

                val displayName = schemSection.getString("name") ?: error("$key.name")
                val icon = parseEnum(Material::class, schemSection.getString("icon") ?: error("$key.icon"))
                val lore = schemSection.getStringList("lore")
                this.schematics[key.lowercase()] = IslandSchematic(key, keyFile, displayName, icon, lore)
                return@forEach
            }

            this.plugin.logger.severe("File located in the schems folder is not a valid file. $keyFile")
        }
    }

    /**
     * Get or create the island world.
     *
     * @param name The name of the world
     * @param enviroment The world enviroment
     * @return The world.
     */
    private fun createWorld(name: String, enviroment: World.Environment): World {
        return Bukkit.getWorld(name) ?: WorldCreator.name(name)
            .type(WorldType.FLAT)
            .environment(enviroment)
            .generateStructures(false)
            .generator(VoidGenerator())
            .createWorld()!!
    }

    /**
     * Check if the world is an island world
     *
     * @param world The world being checked
     * @return if it is an island world.
     */
    fun isIslandWorld(world: World): Boolean {
        return this.worlds.values.map { it.name }.contains(world.name)
    }

    val overworld: World
        get() = this.worlds[World.Environment.NORMAL]!!

    val nether: World
        get() = this.worlds[World.Environment.NETHER]!!

    val end: World
        get() = this.worlds[World.Environment.THE_END]!!

}