package xyz.oribuin.skyblock.manager

import com.google.gson.Gson
import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.database.DataMigration
import dev.rosewood.rosegarden.manager.AbstractDataManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Biome
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import xyz.oribuin.skyblock.database.migration.CreateInitialTables
import xyz.oribuin.skyblock.island.*
import xyz.oribuin.skyblock.manager.ConfigurationManager.Setting
import xyz.oribuin.skyblock.nms.BorderColor
import xyz.oribuin.skyblock.util.getManager
import xyz.oribuin.skyblock.util.getNextIslandLocation
import xyz.oribuin.skyblock.util.parseEnum
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.sql.Connection
import java.sql.Statement
import java.util.*
import java.util.function.Consumer

class DataManager(rosePlugin: RosePlugin) : AbstractDataManager(rosePlugin) {

    val islandCache = mutableMapOf<Int, Island>()
    private val userCache = mutableMapOf<UUID, Member>()
    private val islandReports = mutableSetOf<Report>()

    private val gson = Gson()

    fun loadIslands() {
        this.islandCache.clear()

        this.async { _ ->
            this.databaseConnector.connect {

                val islandQuery = "SELECT key FROM ${this.tablePrefix}islands"
                val result = it.prepareStatement(islandQuery).executeQuery()
                while (result.next()) {
                    this.getIsland(result.getInt(1))
                }
            }
        }
    }

    fun saveIslands() {
        this.databaseConnector.connect {
            this.islandCache.values.forEach { island -> this.saveIsland(island, it) }
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

        this.databaseConnector.connect {
            val insertGroup = "INSERT INTO ${this.tablePrefix}islands (owner) VALUES (?)"

            val islandStatement = it.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS)
            islandStatement.setString(1, owner.toString())

            islandStatement.executeUpdate()
            val keys = islandStatement.generatedKeys
            if (keys.next()) {
                val key = keys.getInt(1)
                val newIsland = Island(
                    key,
                    owner,
                    getNextIslandLocation(
                        key,
                        this.rosePlugin.getManager<WorldManager>().overworld,
                        Setting.ISLAND_SIZE.int
                    )
                )
                val username = Bukkit.getPlayer(owner)?.name ?: "Unknown"
                newIsland.settings.name = "$username's Island"
                newIsland.warp.name = "$username's Warp"
                island = newIsland
                this.saveIsland(newIsland, it) // save the new island that was created.
            }
        }

        return island
    }

    /**
     * The function for deleting an island from the plugin.
     *
     * @param island The island to delete.
     */
    fun deleteIsland(island: Island) {
        this.islandCache.remove(island.key)
        this.islandReports.removeIf { it.island.key == island.key }

        this.async {
            this.databaseConnector.connect {

                val tableNames = listOf("islands", "settings", "members", "warps", "homes", "reports")

                tableNames.forEach { table ->
                    val deleteQuery = "DELETE FROM ${this.tablePrefix}$table WHERE key = ?"
                    val deleteStatement = it.prepareStatement(deleteQuery)
                    deleteStatement.setInt(1, island.key)
                    deleteStatement.executeUpdate()
                }
            }
        }
    }

    fun cacheIsland(island: Island) {
        this.islandCache[island.key] = island

        // Check if it has been 10 minutes since the last save.
        if (island.lastSave + 600000 < System.currentTimeMillis()) {
            this.async { this.databaseConnector.connect { this.saveIsland(island, it) } }
        }

    }

    /**
     * Save an island into the plugin database and cache
     *
     * @param island The island being saved
     */
    private fun saveIsland(island: Island, connection: Connection) {
        island.lastSave = System.currentTimeMillis()
        this.islandCache[island.key] = island

        val main =
            connection.prepareStatement("REPLACE INTO ${this.tablePrefix}islands (`key`, owner, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
        main.setInt(1, island.key)
        main.setString(2, island.owner.toString())
        main.setDouble(3, island.center.blockX.toDouble())
        main.setDouble(4, island.center.blockY.toDouble())
        main.setDouble(5, island.center.blockZ.toDouble())
        main.setFloat(6, island.center.yaw)
        main.setFloat(7, island.center.pitch)
        main.setString(8, island.center.world?.name)
        main.executeUpdate()

        // Save the island settings
        val settings =
            connection.prepareStatement("REPLACE INTO ${this.tablePrefix}settings (`key`, `name`, `public`, mobSpawning, animalSpawning, biome, bans) VALUES (?, ?, ?, ?, ?, ?, ?)")
        settings.setInt(1, island.key)
        settings.setString(2, island.settings.name)
        settings.setBoolean(3, island.settings.public)
        settings.setBoolean(4, island.settings.mobSpawning)
        settings.setBoolean(5, island.settings.animalSpawning)
        settings.setString(6, island.settings.biome.name)
        settings.setString(7, gson.toJson(island.settings.banned))
        settings.executeUpdate()

        // Save the island warps
        val warps =
            connection.prepareStatement("REPLACE INTO ${this.tablePrefix}warps (`key`, `name`, icon, visits, votes, category, disabled, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
        warps.setInt(1, island.key)
        warps.setString(2, island.warp.name)
        warps.setBytes(3, this.serialize(island.warp.icon))
        warps.setInt(4, island.warp.visits)
        warps.setInt(5, island.warp.votes)
        warps.setString(6, gson.toJson(island.warp.category))
        warps.setBoolean(7, island.warp.disabled)
        warps.setDouble(8, island.warp.location.blockX.toDouble())
        warps.setDouble(9, island.warp.location.blockY.toDouble())
        warps.setDouble(10, island.warp.location.blockZ.toDouble())
        warps.setFloat(11, island.warp.location.yaw)
        warps.setFloat(12, island.warp.location.pitch)
        warps.setString(13, island.warp.location.world?.name)
        warps.executeUpdate()

        if (island.home != island.center) {
            val homes =
                connection.prepareStatement("REPLACE INTO ${this.tablePrefix}homes (`key`, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?)")
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
            val members =
                connection.prepareStatement("REPLACE INTO ${this.tablePrefix}members (`key`, player, `username`, `role`, border) VALUES (?, ?, ?, ?, ?)")
            members.setInt(1, island.key)
            members.setString(2, member.uuid.toString())
            members.setString(3, member.username)
            members.setString(4, member.role.name)
            members.setString(5, member.border.name)
            members.executeUpdate()
        }
    }

    /**
     * Save a new report to the database.
     *
     * @param report The report being saved.
     */
    fun saveReport(report: Report) {
        this.islandReports.add(report)

        this.async {
            this.databaseConnector.connect { connection ->
                val insert =
                    connection.prepareStatement("INSERT INTO ${this.tablePrefix}reports (reporter, island, reason, date) VALUES (?, ?, ?, ?)")
                insert.setString(1, report.reporter.toString())
                insert.setInt(2, report.island.key)
                insert.setString(3, report.reason)
                insert.setLong(4, report.date)
                insert.executeUpdate()
            }
        }
    }

    /**
     * Save a member into the cache and the database
     *
     * @param member The member being saved.
     */
    private fun saveMember(member: Member) {
        this.userCache[member.uuid] = member

        this.async { _ ->
            this.databaseConnector.connect {
                val query =
                    "REPLACE INTO ${this.tablePrefix}members (`key`, player, `username`,`role`, border) VALUES (?, ?, ?, ?, ?)"
                val statement = it.prepareStatement(query)
                statement.setInt(1, member.island)
                statement.setString(2, member.uuid.toString())
                statement.setString(3, member.username)
                statement.setString(4, member.role.name)
                statement.setString(5, member.border.name)
                statement.executeUpdate()
            }
        }
    }

    /**
     * Save a member as an extension function
     */
    fun Member.save() = saveMember(this)

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
        this.databaseConnector.connect {
            val query = "SELECT `key`, `role`, `border` FROM ${this.tablePrefix}members WHERE player = ?"
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
            this.databaseConnector.connect {
                val islandQuery = "SELECT owner, x, y, z, world FROM ${this.tablePrefix}islands WHERE key = ?"
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

                val warpQuery =
                    "SELECT name, icon, visits, votes, x, y, z, yaw, pitch, world, category FROM ${this.tablePrefix}warps WHERE key = ?"
                val warpStatement = it.prepareStatement(warpQuery)
                warpStatement.setInt(1, island.key)
                val warpResult = warpStatement.executeQuery()

                // Get the island warp if it exists.
                if (warpResult.next()) {
                    val warp = Warp(
                        island.key,
                        Location(
                            Bukkit.getWorld(warpResult.getString("world")),
                            warpResult.getDouble("x"),
                            warpResult.getDouble("y"),
                            warpResult.getDouble("z")
                        )
                    )
                    warp.name = warpResult.getString("name")
                    warp.icon = this.deserialize(warpResult.getBytes("icon"))
                    warp.visits = warpResult.getInt("visits")
                    warp.votes = warpResult.getInt("votes")
                    warp.category = gson.fromJson(warpResult.getString("category"), Warp.Category::class.java)
                    island.warp = warp
                }

                /**
                 * This is where we get all the island settings and assign it to the island
                 * variable, This comment is entirely to section out everything because I feel like
                 * im gonna die with how cluttered this is.
                 */
                val settingsQuery =
                    "SELECT name, public, mobSpawning, animalSpawning, biome, bans FROM ${this.tablePrefix}settings WHERE key = ?"
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

                val memberQuery = "SELECT * FROM ${this.tablePrefix}members WHERE key = ?"
                val members = mutableListOf<Member>()
                val memberState = it.prepareStatement(memberQuery)
                memberState.setInt(1, island.key)
                val result = memberState.executeQuery()

                // Get all the members for that island.
                while (result.next()) {
                    val player = UUID.fromString(result.getString("player"))
                    val newMember = Member(player)
                    newMember.island = island.key
                    newMember.username = result.getString("username")
                    newMember.role = Member.Role.valueOf(result.getString("role").uppercase())
                    newMember.border = BorderColor.valueOf(result.getString("border").uppercase())
                    this.userCache[player] = newMember
                    members.add(newMember)
                }

                island.members = members

                val homesQuery = "SELECT x, y, z, world, yaw, pitch FROM ${this.tablePrefix}homes WHERE key = ?"
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
        this.rosePlugin.server.scheduler.runTaskAsynchronously(this.rosePlugin, callback)
    }

    override fun getDataMigrations(): MutableList<Class<out DataMigration>> {
        return mutableListOf(CreateInitialTables::class.java)
    }

    /**
     * Serialize an itemstack into a byte array.
     *
     *  @param itemStack The itemstack to serialize.
     *  @return The serialized byte array.
     */
    private fun serialize(itemStack: ItemStack): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)
        dataOutput.writeObject(itemStack)

        return outputStream.toByteArray()
    }

    /**
     * Deserialize an item stack from a byte array.
     *
     * @param bytes The byte array.
     * @return The item stack.
     */
    private fun deserialize(bytes: ByteArray): ItemStack {
        val inputStream = ByteArrayInputStream(bytes)
        val dataInput = BukkitObjectInputStream(inputStream)
        return dataInput.readObject() as ItemStack
    }

}