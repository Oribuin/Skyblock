package xyz.oribuin.skyblock.manager

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.config.CommentedFileConfiguration
import dev.rosewood.rosegarden.manager.Manager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import xyz.oribuin.skyblock.util.copyResourceTo
import xyz.oribuin.skyblock.util.parseEnum
import xyz.oribuin.skyblock.world.IslandSchematic
import xyz.oribuin.skyblock.world.LayeredChunkGenerator
import java.io.File

class WorldManager(rosePlugin: RosePlugin) : Manager(rosePlugin) {

    val worlds = mutableMapOf<World.Environment, World>()
    val schematics = mutableMapOf<String, IslandSchematic>()
    private lateinit var schemConfig: CommentedFileConfiguration

    override fun reload() {
        val section = this.rosePlugin.config.getConfigurationSection("world-names")

        // Check if the world names are defined.
        if (section == null || section.getKeys(false).isEmpty()) {
            this.rosePlugin.logger.severe("World Names weren't defined, cannot continue.")
            Bukkit.getPluginManager().disablePlugin(this.rosePlugin)
            return
        }

        section.getKeys(false).forEach {
            // Match the island name
            val environment = World.Environment.entries.find { env -> env.name.equals(it, true) } ?: return
            // Create the worlds.
            val world = this.createWorld(section.getString(it) ?: ("islands_" + environment.name.lowercase()), environment)
            this.worlds[environment] = world
        }

        val schemFolder = File(this.rosePlugin.dataFolder, "schematics")
        if (!schemFolder.exists())
            schemFolder.mkdir()

        val schemFile = File(this.rosePlugin.dataFolder, "schematics.yml")
        val exists = schemFile.exists()

        this.schemConfig = CommentedFileConfiguration.loadConfiguration(schemFile)
        if (!exists) {
            val defaultFile = File(this.rosePlugin.dataFolder, "schematics/default.schem")
            this.rosePlugin.copyResourceTo("schematics/default.schem", defaultFile)
            this.saveDefaults(this.schemConfig, schemFile)
        }

        val schemFiles = schemFolder.listFiles() ?: error("Schematics folder does not exist.")
        schemConfig.getKeys(false).forEach { key ->
            val keyFile = schemFiles.find { it.nameWithoutExtension.equals(key, true) }

            if (keyFile == null) {
                this.rosePlugin.logger.severe("Could not fine the $key schematic in the schematics folder.")
                return@forEach
            }

            if (ClipboardFormats.findByFile(keyFile) != null) {
                val schemSection = schemConfig.getConfigurationSection(key) ?: error(key)

                val displayName = schemSection.getString("name") ?: error("$key.name")
                val icon = parseEnum(Material::class, schemSection.getString("icon") ?: error("$key.icon"))
                val lore = schemSection.getStringList("lore")
                this.schematics[key.lowercase()] = IslandSchematic(key, keyFile, displayName, icon, lore)
                return@forEach
            }

            this.rosePlugin.logger.severe("File located in the schems folder is not a valid file. $keyFile")
        }
    }

    override fun disable() {
        // Unused
    }

    /**
     * Get or create the island world.
     *
     * @param name The name of the world
     * @param environment The world environment
     * @return The world.
     */
    private fun createWorld(name: String, environment: World.Environment): World {
        return Bukkit.getWorld(name) ?: WorldCreator.name(name)
            .type(WorldType.FLAT)
            .environment(environment)
            .generateStructures(false)
            .generator(LayeredChunkGenerator())
            .createWorld() ?: throw Error("Couldn't create the world.")
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

    /**
     * Save the default schematics.
     *
     * @param config The config to save to.
     */
    private fun saveDefaults(config: CommentedFileConfiguration, file: File) {
        val section = config.createSection("default")
        section["name"] = "#a6b2fc&lPlains"
        section["icon"] = "GRASS_BLOCK"
        section["lore"] = listOf(
            " &f| &7This is the main starter",
            " &f| &7set in the plains biome!",
            " &f|", " &f| &7Click to create this island."
        )

        config.save(file);
    }

    val overworld: World
        get() = this.worlds[World.Environment.NORMAL]!!

    val nether: World
        get() = this.worlds[World.Environment.NETHER]!!

    val end: World
        get() = this.worlds[World.Environment.THE_END]!!

}