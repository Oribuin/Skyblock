package xyz.oribuin.skyblock.database.migration

import dev.rosewood.rosegarden.database.DataMigration
import dev.rosewood.rosegarden.database.DatabaseConnector
import dev.rosewood.rosegarden.database.MySQLConnector
import java.sql.Connection

class CreateInitialTables : DataMigration(1) {

    override fun migrate(connector: DatabaseConnector, connection: Connection, tablePrefix: String) {
        val increment = if (connector is MySQLConnector) " AUTO_INCREMENT" else ""
        // The table for saving island location.
        val islandDB = "CREATE TABLE IF NOT EXISTS ${tablePrefix}islands (" +
                "`key` INTEGER PRIMARY KEY$increment, " +
                "owner VARCHAR(36), " +
                "`x` DOUBLE, " +
                "`y` DOUBLE, " +
                "`z` DOUBLE, " +
                "`yaw` FLOAT, " +
                "`pitch` FLOAT, " +
                "world TEXT)"

        connection.prepareStatement(islandDB).executeUpdate()

        // The table for saving the island settings.
        val settingsDB = "CREATE TABLE IF NOT EXISTS ${tablePrefix}settings (" +
                "`key` INT, " +
                "`name` TEXT, " +
                "`public` BOOLEAN DEFAULT true, " +
                "mobSpawning BOOLEAN DEFAULT true, " +
                "animalSpawning BOOLEAN DEFAULT true, " +
                "biome TEXT, " +
                "bans TEXT, " +
                "PRIMARY KEY(key))"

        connection.prepareStatement(settingsDB).executeUpdate()

        // The table for the island members
        val membersDB = "CREATE TABLE IF NOT EXISTS ${tablePrefix}members (" +
                "`key` INT, " +
                "player VARCHAR(36), " +
                "role VARCHAR(36), " +
                "border TEXT, " +
                "PRIMARY KEY(player))"

        connection.prepareStatement(membersDB).executeUpdate()

        // The table for the island warps
        val warpsDB = "CREATE TABLE IF NOT EXISTS ${tablePrefix}warps (" +
                "key INT, " +
                "name TEXT, " +
                "icon VARBINARY(2456) NOT NULL, " +
                "visits INT DEFAULT 0, " +
                "`votes` INT DEFAULT 0, " +
                "category TEXT, " +
                "disabled BOOLEAN DEFAULT false, " +
                "`x` DOUBLE, " + // Location of the warp.
                "`y` DOUBLE, " +
                "`z` DOUBLE, " +
                "`yaw` FLOAT, " +
                "`pitch` FLOAT, " +
                "`world` TEXT, " +
                "PRIMARY KEY(key))"

        connection.prepareStatement(warpsDB).executeUpdate();

        val homesDB = "CREATE TABLE IF NOT EXISTS ${tablePrefix}homes (" +
                "key INT, " +
                "x DOUBLE, " +
                "y DOUBLE, " +
                "z DOUBLE, " +
                "world TEXT, " +
                "yaw FLOAT, " +
                "pitch FLOAT, " +
                "PRIMARY KEY(key))"

        connection.prepareStatement(homesDB).executeUpdate()

        val reports = "CREATE TABLE IF NOT EXISTS ${tablePrefix}reports (" +
                "reporter VARCHAR(36), " +
                "island INT, " +
                "reason TEXT, " +
                "`date` LONG)"

        connection.prepareStatement(reports).executeUpdate()
    }

}