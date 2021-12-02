package xyz.oribuin.skyblock.manager

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.scheduler.BukkitTask
import xyz.oribuin.orilibrary.database.MySQLConnector
import xyz.oribuin.orilibrary.manager.DataHandler
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.*
import xyz.oribuin.skyblock.nms.BorderColor
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.getNextIslandLocation
import xyz.oribuin.skyblock.util.parseEnum
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
            val increment = if (connector is MySQLConnector) " AUTO_INCREMENT" else ""
            this.connector.connect {

                // The table for saving island location.
                val islandDB = "CREATE TABLE IF NOT EXISTS ${tableName}_islands (" +
                        "`key` INTEGER PRIMARY KEY$increment, " +
                        "owner VARCHAR(36), " +
                        "`x` DOUBLE, " +
                        "`y` DOUBLE, " +
                        "`z` DOUBLE, " +
                        "world TEXT)"

                it.prepareStatement(islandDB).executeUpdate()

                // The table for saving the island settings.
                val settingsDB = "CREATE TABLE IF NOT EXISTS ${tableName}_settings (" +
                        "`key` INT, " +
                        "`name` TEXT, " +
                        "`public` BOOLEAN DEFAULT true, " +
                        "mobSpawning BOOLEAN DEFAULT true, " +
                        "animalSpawning BOOLEAN DEFAULT true, " +
                        "biome TEXT, " +
                        "bans TEXT, " +
                        "PRIMARY KEY(key))"

                it.prepareStatement(settingsDB).executeUpdate()

                // The table for saving the island upgrades.
                val upgradesDB = "CREATE TABLE IF NOT EXISTS ${tableName}_upgrades (" +
                        "`key` INT, " +
                        "islandFly BOOLEAN DEFAULT false, " +
                        "sizeTier INT, " +
                        "chestGenTier INT, " +
                        "PRIMARY KEY (key))"

                it.prepareStatement(upgradesDB).executeUpdate()

                // The table for the island members
                val membersDB = "CREATE TABLE IF NOT EXISTS ${tableName}_members (" +
                        "`key` INT, " +
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
                        "description TEXT," + // Will likely have to convert this into a string.
                        "visits INT DEFAULT 0, " +
                        "`votes` INT DEFAULT 0, " +
                        "`public` BOOLEAN DEFAULT false, " +
                        "category TEXT, " +
                        "`x` DOUBLE, " + // Location of the warp.
                        "`y` DOUBLE, " +
                        "`z` DOUBLE, " +
                        "`world` TEXT, " +
                        "PRIMARY KEY(key))"

                it.prepareStatement(warpsDB).executeUpdate();

                val homesDB = "CREATE TABLE IF NOT EXISTS ${tableName}_homes (" +
                        "key INT, " +
                        "x DOUBLE, " +
                        "y DOUBLE, " +
                        "z DOUBLE, " +
                        "world TEXT, " +
                        "yaw FLOAT, " +
                        "pitch FLOAT, " +
                        "PRIMARY KEY(key))"

                it.prepareStatement(homesDB).executeUpdate()
            }
        }
    }

    /**
     * The function for creating a new island in the plugin, separate from the saving item due to island key.
     *
     * @param owner The owner of the island
     * @return The new island.
     */
    fun createIsland(owner: UUID): Island {
        lateinit var island: Island
        this.connector.connect {
            val insertGroup = "INSERT INTO ${tableName}_islands (owner) VALUES (?)"

            val islandStatement = it.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS)
            islandStatement.setString(1, owner.toString())

            islandStatement.executeUpdate()
            val keys = islandStatement.generatedKeys
            if (keys.next()) {
                val key = keys.getInt(1)
                val newIsland = Island(key, owner, getNextIslandLocation(key, plugin.getManager<WorldManager>().overworld, 350))
                val username = Bukkit.getPlayer(owner)?.name ?: "Unknown"
                newIsland.settings.name = "$username's Island"
                newIsland.warp.name = "$username's Warp"
                island = newIsland
                this.saveIsland(newIsland) // save the new island that was created.
            }
        }

        return island
    }

    /**
     * Save an island into the plugin database and cache
     *
     * @param island The island being saved
     */
    fun saveIsland(island: Island) {
        this.islandCache[island.key] = island

        // Get the remade connection or make a new one.
        this.async { _ ->
            this.connector.connect {

                val main = it.prepareStatement("REPLACE INTO ${tableName}_islands (`key`, owner, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?)")
                main.setInt(1, island.key)
                main.setString(2, island.owner.toString())
                main.setDouble(3, island.center.blockX.toDouble())
                main.setDouble(4, island.center.blockY.toDouble())
                main.setDouble(5, island.center.blockZ.toDouble())
                main.setString(6, island.center.world?.name)
                main.executeUpdate()

                // Save the island settings
                val settings = it.prepareStatement("REPLACE INTO ${tableName}_settings (`key`, `name`, `public`, mobSpawning, animalSpawning, biome, bans) VALUES (?, ?, ?, ?, ?, ?, ?)")
                settings.setInt(1, island.key)
                settings.setString(2, island.settings.name)
                settings.setBoolean(3, island.settings.public)
                settings.setBoolean(4, island.settings.mobSpawning)
                settings.setBoolean(5, island.settings.animalSpawning)
                settings.setString(6, island.settings.biome.name)
                settings.setString(7, gson.toJson(island.settings.banned))
                settings.executeUpdate()

                // Save the island upgrades.
                val upgrades = it.prepareStatement("REPLACE INTO ${tableName}_upgrades (`key`, islandFly, sizeTier, chestGenTier) VALUES (?, ?, ?, ?)")
                upgrades.setInt(1, island.key)
                upgrades.setBoolean(2, island.upgrade.islandFly)
                upgrades.setInt(3, island.upgrade.sizeTier)
                upgrades.setInt(4, island.upgrade.chestGenTier)
                upgrades.executeUpdate()

                // Save the island warps
                val warps = it.prepareStatement("REPLACE INTO ${tableName}_warps (`key`, `name`, icon, description, visits, votes, `public`, category, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                warps.setInt(1, island.key)
                warps.setString(2, island.warp.name)
                warps.setString(3, island.warp.icon.name)
                warps.setString(4, gson.toJson(island.warp.desc))
                warps.setInt(5, island.warp.visits)
                warps.setInt(6, island.warp.votes)
                warps.setBoolean(7, island.warp.public)
                warps.setString(8, island.warp.category.name)
                warps.setDouble(9, island.warp.location.blockX.toDouble())
                warps.setDouble(10, island.warp.location.blockY.toDouble())
                warps.setDouble(11, island.warp.location.blockZ.toDouble())
                warps.setString(12, island.warp.location.world?.name)
                warps.executeUpdate()

                if (island.home != island.center) {
                    val homes = it.prepareStatement("REPLACE INTO ${tableName}_homes (`key`, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?)")
                    homes.setInt(1, island.key)
                    homes.setDouble(2, island.home.x)
                    homes.setDouble(3, island.home.y)
                    homes.setDouble(4, island.home.z)
                    homes.setFloat(5, island.home.yaw)
                    homes.setFloat(6, island.home.pitch)
                    homes.setString(7, island.home.world?.name)
                    homes.executeUpdate()
                }

                // This could be pretty bad but who cares.
                island.members.forEach { member ->
                    val members = it.prepareStatement("REPLACE INTO ${tableName}_members (`key`, player, `role`, border) VALUES (?, ?, ?, ?)")
                    members.setInt(1, island.key)
                    members.setString(2, member.uuid.toString())
                    members.setString(3, member.role.name)
                    members.setString(4, member.border.name)
                    members.executeUpdate()
                }
            }
        }
    }

    /**
     * Save a member into the cache and the database
     *
     * @param member The member being saved.
     */
    fun saveMember(member: Member) {
        this.userCache[member.uuid] = member
        this.async { _ ->
            this.connector.connect {
                val query = "REPLACE INTO ${tableName}_members (`key`, player, `role`, border) VALUES (?, ?, ?, ?)"
                val statement = it.prepareStatement(query)
                statement.setInt(1, member.island)
                statement.setString(2, member.uuid.toString())
                statement.setString(3, member.role.name)
                statement.setString(4, member.border.name)
                statement.executeUpdate()
            }
        }
    }

    /**
     * Get a member from the user's UUID.
     *
     * @param player The player's unique user id
     * @return The island member.
     */
    fun getMember(player: UUID): Member {
        val member = this.userCache[player]
        if (member != null)
            return member

        val newMember = Member(player)
        this.connector.connect {
            val query = "SELECT `key`, `role`, `border` FROM ${tableName}_members WHERE player = ?"
            val statement = it.prepareStatement(query)
            statement.setString(1, player.toString())
            val result = statement.executeQuery();

            if (result.next()) {
                newMember.island = result.getInt("key")
                newMember.role = Member.Role.valueOf(result.getString("role").uppercase())
                newMember.border = BorderColor.valueOf(result.getString("border").uppercase())
                this.userCache[player] = newMember
            }
        }

        return newMember
    }

    /**
     * Get an island from the island key.
     *
     * @param key The island key
     * @return The island with the matching key or no island.
     */
    fun getIsland(key: Int): Island? {
        if (key == -1)
            return null;

        val cachedIsland = this.islandCache[key]
        if (cachedIsland != null)
            return cachedIsland

        this.async { _ ->
            this.connector.connect {
                val islandQuery = "SELECT owner, x, y, z, world FROM ${tableName}_islands WHERE key = ?"
                val islandStatement = it.prepareStatement(islandQuery)
                islandStatement.setInt(1, key)
                val islandResult = islandStatement.executeQuery()

                var island: Island? = null
                if (islandResult.next()) {
                    val owner = islandResult.getString("owner")
                    val world = islandResult.getString("world")
                    val x = islandResult.getDouble("x")
                    val y = islandResult.getDouble("y")
                    val z = islandResult.getDouble("z")
                    island = Island(key, UUID.fromString(owner), Location(Bukkit.getWorld(world), x, y, z))
                }

                if (island == null)
                    return@connect

                val warpQuery = "SELECT name, icon, description, visits, votes, x, y, z, world, `public`, category FROM ${tableName}_warps WHERE key = ?"
                val warpStatement = it.prepareStatement(warpQuery)
                warpStatement.setInt(1, island.key)
                val warpResult = warpStatement.executeQuery()

                // Get the island warp if it exists.
                if (warpResult.next()) {
                    val warp = Warp(island.key, Location(Bukkit.getWorld(warpResult.getString("world")), warpResult.getDouble("x"), warpResult.getDouble("y"), warpResult.getDouble("z")))
                    warp.name = warpResult.getString("name")
                    warp.icon = parseEnum(Material::class, warpResult.getString("icon"))
                    warp.desc = gson.fromJson(warpResult.getString("description"), Warp.Desc::class.java)
                    warp.visits = warpResult.getInt("visits")
                    warp.votes = warpResult.getInt("votes")
                    warp.public = warpResult.getBoolean("public")
                    warp.category = parseEnum(Warp.Category::class, warpResult.getString("category"))
                    island.warp = warp
                }

                /**
                 * This is where we get all the island settings and assign it to the island
                 * variable, This comment is entirely to section out everything because I feel like
                 * im gonna die with how cluttered this is.
                 */
                val settingsQuery = "SELECT name, public, mobSpawning, animalSpawning, biome, bans FROM ${tableName}_settings WHERE key = ?"
                val settingsState = it.prepareStatement(settingsQuery)
                settingsState.setInt(1, island.key)
                val settingsResult = settingsState.executeQuery()

                // Get any island settings if they exist.
                if (settingsResult.next()) {
                    val settings = Settings(key)
                    settings.name = settingsResult.getString("name")
                    settings.public = settingsResult.getBoolean("public")
                    settings.mobSpawning = settingsResult.getBoolean("mobSpawning")
                    settings.animalSpawning = settingsResult.getBoolean("animalSpawning")
                    settings.biome = parseEnum(Biome::class, settingsResult.getString("biome") ?: "PLAINS")
                    settings.banned = gson.fromJson(settingsResult.getString("bans"), Settings.Banned::class.java)
                    island.settings = settings
                }

                val upgradesQuery = "SELECT islandFly, sizeTier, chestGenTier FROM ${tableName}_upgrades WHERE key = ?"
                val upgradesState = it.prepareStatement(upgradesQuery)
                upgradesState.setInt(1, island.key)
                val upgradesResult = upgradesState.executeQuery()

                // Get any island upgrades if they don't exist
                if (upgradesResult.next()) {
                    val upgrade = Upgrade(island.key)
                    upgrade.islandFly = upgradesResult.getBoolean("islandFly")
                    upgrade.chestGenTier = upgradesResult.getInt("chestGenTier")
                    upgrade.sizeTier = upgradesResult.getInt("sizeTier")
                }

                val memberQuery = "SELECT player, role, border FROM ${tableName}_members WHERE key = ?"
                val members = mutableListOf<Member>()
                val memberState = it.prepareStatement(memberQuery)
                memberState.setInt(1, island.key)
                val result = memberState.executeQuery()

                // Get all the members for that island.
                while (result.next()) {
                    val player = UUID.fromString(result.getString("player"))
                    val newMember = Member(player)
                    newMember.island = island.key
                    newMember.role = Member.Role.valueOf(result.getString("role").uppercase())
                    newMember.border = BorderColor.valueOf(result.getString("border").uppercase())
                    this.userCache[player] = newMember
                    members.add(newMember)
                }

                island.members = members

                val homesQuery = "SELECT x, y, z, world, yaw, pitch FROM ${tableName}_homes WHERE key = ?"
                val homesState = it.prepareStatement(homesQuery)
                homesState.setInt(1, key)
                val homesResult = homesState.executeQuery()
                if (homesResult.next()) {
                    val world = Bukkit.getWorld(homesResult.getString("world"))
                    val x = homesResult.getDouble("x")
                    val y = homesResult.getDouble("y")
                    val z = homesResult.getDouble("z")
                    val yaw = homesResult.getFloat("yaw")
                    val pitch = homesResult.getFloat("pitch")
                    island.home = Location(world, x, y, z, yaw, pitch)
                }

                this.islandCache[key] = island
            }
        }

        return this.islandCache[key]
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