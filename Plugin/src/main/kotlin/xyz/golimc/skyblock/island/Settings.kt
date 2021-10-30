package xyz.golimc.skyblock.island

import net.minecraft.world.entity.ai.behavior.BehaviorHome
import org.bukkit.Location
import org.bukkit.block.Biome

class Settings(val key: Int) {
    var name: String = "Unknown" // The name of the island
    var public: Boolean = true // Is the island open to the public
    var mobSpawning: Boolean = true // Can hostile mobs naturally spawn
    var animalSpawning: Boolean = true // Can non hostile mobs naturally spawn
    var biome: Biome = Biome.PLAINS // The biome of the island.
}