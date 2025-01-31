package dev.oribuin.skyblock.database.migration;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import dev.rosewood.rosegarden.database.MySQLConnector;

import java.sql.Connection;
import java.sql.SQLException;

public class CreateInitialTables extends DataMigration {

    public CreateInitialTables() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {

        String islandDB = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "islands (" +
                "`key` INTEGER PRIMARY KEY" + increment(connector) + ", " +
                "owner VARCHAR(36), " +
                "`x` DOUBLE, " +
                "`y` DOUBLE, " +
                "`z` DOUBLE, " +
                "`yaw` FLOAT, " +
                "`pitch` FLOAT, " +
                "world TEXT)";

        connection.prepareStatement(islandDB).executeUpdate();

        // The table for saving the island settings.
        String settingsDB = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "settings (" +
                "`key` INT, " +
                "`name` TEXT, " +
                "`public` BOOLEAN DEFAULT true, " +
                "mobSpawning BOOLEAN DEFAULT true, " +
                "animalSpawning BOOLEAN DEFAULT true, " +
                "biome TEXT, " +
                "bans TEXT, " +
                "PRIMARY KEY(key))";

        connection.prepareStatement(settingsDB).executeUpdate();

        // The table for the island members
        String membersDB = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "members (" +
                "`key` INT, " +
                "player VARCHAR(36), " +
                "username VARCHAR(24), " +
                "`role` VARCHAR(36), " +
                "border TEXT, " +
                "PRIMARY KEY(player))";

        connection.prepareStatement(membersDB).executeUpdate();

        // The table for the island warps
        String warpsDB = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "warps (" +
                "`key` INT, " +
                "`name` TEXT, " +
                "icon VARBINARY(2456) NOT NULL, " +
                "category TEXT, " +
                "`x` DOUBLE, " + // Location of the warp.
                "`y` DOUBLE, " +
                "`z` DOUBLE, " +
                "`yaw` FLOAT, " +
                "`pitch` FLOAT, " +
                "`world` TEXT, " +
                "PRIMARY KEY(key))";

        connection.prepareStatement(warpsDB).executeUpdate();

        String homesDB = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "homes (" +
                "`key` INT, " +
                "x DOUBLE, " +
                "y DOUBLE, " +
                "z DOUBLE, " +
                "world TEXT, " +
                "yaw FLOAT, " +
                "pitch FLOAT, " +
                "PRIMARY KEY(key))";

        connection.prepareStatement(homesDB).executeUpdate();
    }

    public String increment(DatabaseConnector connector) {
        return connector instanceof MySQLConnector ? " AUTO_INCREMENT" : "";
    }


}