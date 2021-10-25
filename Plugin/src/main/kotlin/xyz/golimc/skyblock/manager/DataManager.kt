package xyz.golimc.skyblock.manager

import org.bukkit.scheduler.BukkitTask
import xyz.golimc.skyblock.SkyblockPlugin
import xyz.golimc.skyblock.island.Island
import xyz.oribuin.orilibrary.manager.DataHandler
import java.util.function.Consumer

class DataManager(private val plugin: SkyblockPlugin) : DataHandler(plugin) {

    val islandCache = mutableMapOf<Int, Island>()

    override fun enable() {
        super.enable()

        this.async { _ ->
            this.connector.connect {

                // The table for saving island location.
                val islandDB = "CREATE TABLE IF NOT EXISTS ${tableName}_islands (" +
                        "key INT NOT NULL AUTO_INCREMENT, " +
                        "owner VARCHAR(36), " +
                        "centerX DOUBLE, " +
                        "centerY DOUBLE, " +
                        "centerZ DOUBLE, " +
                        "centerWorld TEXT, " +
                        "PRIMARY KEY(key))"

                it.prepareStatement(islandDB).executeUpdate()

                // The table for saving the island settings.
                val settingsDB = "CREATE TABLE IF NOT EXISTS ${tableName}_settings (" +
                        "key INT, " +
                        "name TEXT, " +
                        "public BOOLEAN, " +
                        "mobSpawning BOOLEAN, " +
                        "animalSpawning BOOLEAN, " +
                        "biome TEXT, " +
                        "PRIMARY KEY(key))"

                it.prepareStatement(settingsDB).executeUpdate()

                // The table for saving the island upgrades.
                val upgradesDB = "CREATE TABLE IF NOT EXISTS ${tableName}_upgrades (" +
                        "key INT, " +
                        "islandFly BOOLEAN, " +
                        "sizeTier INT, " +
                        "chestGenTier INT, " +
                        "memberTier INT, " +
                        "PRIMARY KEY (key))"

                it.prepareStatement(upgradesDB).executeUpdate()

                // The table for the island members
                val membersDB = "CREATE TABLE IF NOT EXISTS ${tableName}_members (" +
                        "key INT, " +
                        "player VARCHAR(36), " +
                        "role VARCHAR(36), " +
                        "border TEXT, " +
                        "PRIMARY KEY(player))"

                it.prepareStatement(membersDB).executeUpdate()

                // The table for the island warps
                val warpsDB = "CREATE TABLE IF NOT EXISTS ${tableName}_warps (" +
                        "key INT, " +
                        "name TEXT, " +
                        "icon TEXT, " +
                        "description LONGTEXT," + // Will likely have to convert this into a string.
                        "visits INT, " +
                        "votes INT, " +
                        "PRIMARY KEY(key))"
            }
        }
    }

    /**
     * Run a task asynchronously.
     *
     * @param callback The task callback.
     */
    private fun async(callback: Consumer<BukkitTask>) {
        this.plugin.server.scheduler.runTaskAsynchronously(this.plugin, callback)
    }

}