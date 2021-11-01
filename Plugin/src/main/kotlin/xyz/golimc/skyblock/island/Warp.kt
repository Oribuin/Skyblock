package xyz.golimc.skyblock.island

import org.bukkit.Location
import org.bukkit.Material

data class Warp(val key: Int, var location: Location) {
    var name: String = "Unknown Name" // The name of the warp
    var icon: Material = Material.GRASS_BLOCK // The icon for the warp
    var desc: Desc = Desc() // The description for the warp
    var visits: Int = 0 // The amount of times the warp has been visited, just for some extra spice.
    var votes: Int = 0 //The amount of votes that an island has.

    data class Desc(val desc: MutableList<String> = mutableListOf())
}
