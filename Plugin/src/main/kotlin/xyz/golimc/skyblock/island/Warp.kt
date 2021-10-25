package xyz.golimc.skyblock.island

import org.bukkit.Location
import org.bukkit.Material
import java.util.*

data class Warp(val key: Int) {
    var location: Location? = null
    var name: String = "Unknown Name" // The name of the warp
    var icon: Material = Material.GRASS_BLOCK // The icon for the warp
    var desc: MutableList<String> = mutableListOf() // The description for the warp
    var owner: UUID = UUID.randomUUID() //The owner of the warp
    var visits: Int = 0 // The amount of times the warp has been visited, just for some extra spice.
    var votes: Int = 0 //The amount of votes that an island has.
}
