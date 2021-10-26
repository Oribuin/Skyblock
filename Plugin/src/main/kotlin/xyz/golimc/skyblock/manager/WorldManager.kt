package xyz.golimc.skyblock.manager

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import oshi.util.FileUtil
import xyz.golimc.skyblock.SkyblockPlugin
import xyz.golimc.skyblock.world.IslandSchematic
import xyz.golimc.skyblock.world.VoidGenerator
import xyz.oribuin.orilibrary.manager.Manager
import xyz.oribuin.orilibrary.util.FileUtils
import java.io.File

class WorldManager(private val plugin: SkyblockPlugin) : Manager(plugin) {

    val worlds = mutableMapOf<World.Environment, World>()
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
            val file = schemFiles.find { it.nameWithoutExtension.equals(key, true) }
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

}