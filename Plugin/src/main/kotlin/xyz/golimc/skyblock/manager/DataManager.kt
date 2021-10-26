package xyz.golimc.skyblock.manager

import com.google.gson.Gson
import org.bukkit.Location
import org.bukkit.scheduler.BukkitTask
import xyz.golimc.skyblock.SkyblockPlugin
import xyz.golimc.skyblock.island.Island
import xyz.golimc.skyblock.island.Member
import xyz.oribuin.orilibrary.database.MySQLConnector
import xyz.oribuin.orilibrary.manager.DataHandler
import java.sql.Connection
import java.sql.Statement
import java.util.*
import java.util.function.Consumer

class DataManager(private val plugin: SkyblockPlugin) : DataHandler(plugin) {

    val islandCache = mutableMapOf<Int, Island>()
    val userCache = mutableMapOf<UUID, Member>()
    private val gson = Gson()

    override fun enable() {
        super.enable()

        this.async { _ ->
            val increment = if (connector is MySQLConnector) "AUTO_INCREMENT" else ""
            this.connector.connect {

                // The table for saving island location.
                val islandDB = "CREATE TABLE IF NOT EXISTS ${tableName}_islands (" +
                        "key INT NOT NULL $increment, " +
                        "owner VARCHAR(36), " +
                        "x DOUBLE, " +
                        "y DOUBLE, " +
                        "z DOUBLE, " +
                        "world TEXT, " +
                        "PRIMARY KEY(key))"

                it.prepareStatement(islandDB).executeUpdate()

                // The table for saving the island settings.
                val settingsDB = "CREATE TABLE IF NOT EXISTS ${tableName}_settings (" +
                        "key INT NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "public BOOLEAN DEFAULT true, " +
                        "mobSpawning BOOLEAN DEFAULT true, " +
                        "animalSpawning BOOLEAN DEFAULT true, " +
                        "biome TEXT NOT NULL, " +
                        "PRIMARY KEY(key))"

                it.prepareStatement(settingsDB).executeUpdate()

                // The table for saving the island upgrades.
                val upgradesDB = "CREATE TABLE IF NOT EXISTS ${tableName}_upgrades (" +
                        "key INT NOT NULL, " +
                        "islandFly BOOLEAN DEFAULT false, " +
                        "sizeTier INT NOT NULL, " +
                        "chestGenTier INT NOT NULL, " +
                        "memberTier INT NOT NULL, " +
                        "PRIMARY KEY (key))"

                it.prepareStatement(upgradesDB).executeUpdate()

                // The table for the island members
                val membersDB = "CREATE TABLE IF NOT EXISTS ${tableName}_members (" +
                        "key INT NOT NULL, " +
                        "player VARCHAR(36) NOT NULL, " +
                        "role VARCHAR(36) NOT NULL, " +
                        "border TEXT NOT NULL , " +
                        "PRIMARY KEY(player))"

                it.prepareStatement(membersDB).executeUpdate()

                // The table for the island warps
                val warpsDB = "CREATE TABLE IF NOT EXISTS ${tableName}_warps (" +
                        "key INT NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "icon TEXT NOT NULL, " +
                        "description LONGTEXT NOT NULL," + // Will likely have to convert this into a string.
                        "visits INT DEFAULT 0, " +
                        "votes INT DEFAULT 0, " +
                        "x DOUBLE, " + // Location of the warp.
                        "y DOUBLE, " +
                        "z DOUBLE, " +
                        "world TEXT NOT NULL, " +
                        "PRIMARY KEY(key))"

                it.prepareStatement(warpsDB).executeUpdate();
            }
        }
    }

    /**
     * The function for creating a new island in the plugin, separate from the saving item due to island key.
     *
     * @param owner The owner of the island
     * @param loc The location of the island
     * @return The new island.
     */
    fun createIsland(owner: UUID, loc: Location): Island? {
        var island: Island? = null
        this.connector.connect {
            val insertGroup = "INSERT INTO ${tableName}_islands (owner, x, y, z, world) VALUES (?, ?, ?, ?)"

            val islandStatement = it.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS)
            islandStatement.setString(1, owner.toString())
            islandStatement.setDouble(2, loc.blockX.toDouble())
            islandStatement.setDouble(3, loc.blockY.toDouble())
            islandStatement.setDouble(4, loc.blockZ.toDouble())
            islandStatement.setString(5, loc.world?.name)

            islandStatement.executeUpdate()
            if (islandStatement.generatedKeys.next()) {
                val newIsland = Island(islandStatement.generatedKeys.getInt(1), owner, loc)
                island = newIsland
                this.saveIsland(newIsland, it) // save the new island that was created.
            }
        }

        return island
    }

    /**
     * Save an island into the plugin database and cache
     *
     * @param island The island being saved
     * @param connection The connection, Incas we wanna run it alongside a pre-existing connection, lets not open multiple connections here people.
     */
    fun saveIsland(island: Island, connection: Connection? = null) {
        this.islandCache[island.key] = island

        // Get the remade connection or make a new one.
        this.async { _ ->
            (connection ?: this.connector.connect {

                // Save the island settings
                val settings = it.prepareStatement("REPLACE INTO ${tableName}_settings (key, name, public, mobSpawning, animalSpawning, biome) VALUES (?, ?, ?, ?, ?, ?)")
                settings.setInt(1, island.key)
                settings.setString(2, island.settings.name)
                settings.setBoolean(2, island.settings.public)
                settings.setBoolean(2, island.settings.mobSpawning)
                settings.setBoolean(2, island.settings.animalSpawning)
                settings.setString(2, island.settings.biome.name)
                settings.executeUpdate()

                // Save the island upgrades.
                val upgrades = it.prepareStatement("REPLACE INTO ${tableName}_upgrades (key, islandFly, sizeTier, chestGenTier, memberTier), VALUES (?, ?, ?, ?, ?)")
                upgrades.setInt(1, island.key)
                upgrades.setBoolean(2, island.upgrades.islandFly)
                upgrades.setInt(2, island.upgrades.sizeTier)
                upgrades.setInt(2, island.upgrades.chestGenTier)
                upgrades.setInt(2, island.upgrades.memberTier)
                upgrades.executeUpdate()

                // Save the island warps
                val warps = it.prepareStatement("REPLACE INTO ${tableName}_key (key, name, icon, description, visits, votes, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                warps.setInt(1, island.key)
                warps.setString(2, island.warp.name)
                warps.setString(3, island.warp.icon.name)
                warps.setString(4, gson.toJson(island.warp.desc))
                warps.setInt(5, island.warp.visits)
                warps.setInt(6, island.warp.votes)
                warps.setDouble(7, island.warp.location.blockX.toDouble())
                warps.setDouble(8, island.warp.location.blockY.toDouble())
                warps.setDouble(9, island.warp.location.blockZ.toDouble())
                warps.setString(10, island.warp.location.world?.name)
                warps.executeUpdate()

                // This could be pretty bad but who cares.
                island.members.forEach { member ->
                    val members = it.prepareStatement("REPLACE INTO ${tableName}_members (key, player, role, border) VALUES (?, ?, ?, ?)")
                    members.setInt(1, island.key)
                    members.setString(2, member.uuid.toString())
                    members.setString(3, member.role.name)
                    members.setString(4, member.border.name)
                    members.executeUpdate()
                }
            })
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