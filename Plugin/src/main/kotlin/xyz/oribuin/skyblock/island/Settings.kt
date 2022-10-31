package xyz.oribuin.skyblock.island

import java.util.*
import org.bukkit.block.Biome

class Settings(val key: Int) {
    var name: String = "Unknown" // The name of the island
    var public: Boolean = true // Is the island open to the public
    var mobSpawning: Boolean = true // Can hostile mobs naturally spawn
    var animalSpawning: Boolean = true // Can non-hostile mobs naturally spawn
    var biome: Biome = Biome.PLAINS // The biome of the island.
    var banned: Banned = Banned() // The users who are banned from the island.

    data class Banned(private val bans: MutableMap<String, Long> = mutableMapOf()) {

        /**
         * Get the list of banned users.
         *
         * @return The list of banned users.
         */
        fun getUUIDs(): List<UUID> {
            return bans.map { UUID.fromString(it.key) }
        }

        /**
         * Get the time the user was banned.
         *
         * @param uuid The UUID of the user.
         * @return The time the user was banned.
         */
        fun getTimeBanned(uuid: UUID): Long {
            return bans[uuid.toString()] ?: 0
        }

        /**
         * Check if the user is banned.
         *
         * @param uuid The UUID of the user.
         * @return True if the user is banned.
         */
        fun isBanned(uuid: UUID): Boolean {
            return bans.containsKey(uuid.toString())
        }


    }
}