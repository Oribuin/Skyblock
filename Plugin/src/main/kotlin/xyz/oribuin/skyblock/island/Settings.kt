package xyz.oribuin.skyblock.island

import net.minecraft.world.entity.ai.behavior.BehaviorHome
import org.bukkit.Location
import org.bukkit.block.Biome
import java.util.*

class Settings(val key: Int) {
    var name: String = "Unknown" // The name of the island
    var public: Boolean = true // Is the island open to the public
    var mobSpawning: Boolean = true // Can hostile mobs naturally spawn
    var animalSpawning: Boolean = true // Can non-hostile mobs naturally spawn
    var biome: Biome = Biome.PLAINS // The biome of the island.
    var banned: Banned = Banned() // The users who are banned from the island.

    data class Banned(val bans: MutableList<String> = mutableListOf()) {
        val uuids = bans.map { UUID.fromString(it) }
    }
}