package xyz.oribuin.skyblock.island

import org.bukkit.Location
import org.bukkit.Material
import java.util.*

data class Warp(val key: Int, var location: Location) {
    var name: String = "" // The name of the warp
    var icon: Material = Material.GRASS_BLOCK // The icon for the warp
    var desc: Desc = Desc() // The description for the warp
    var visits: Int = 0 // The amount of times the warp has been visited, just for some extra spice.
    var votes: Int = 0 //The amount of votes that an island has.
    var category: Category = Category.GENERAL // The island category

    // A way to prevent people insta farming votes & visits constantly without requiring a database.
    val votedUsers = mutableListOf<UUID>() // Cache users who have upvoted the island
    val visitUsers = mutableListOf<UUID>() // Cache the users who have tp'd to the island already

    data class Desc(val text: MutableList<String> = mutableListOf("No Description Set."))

    enum class Category(val icon: Material, val desc: List<String>, val slot: Int) {
        GENERAL(Material.NAME_TAG, listOf(" &f| &7General islands with", " &f| &7multiple purposes."), 12), // A vague island category
        FARMS(Material.DIAMOND_HOE, listOf(" &f| &7Islands with public farms", " &f| &7for anyone to use."), 13), // Islands dedicated to farming, XP, Crops, Mob Loot, Etc
        PARKOUR(Material.FEATHER, listOf(" &f| &7Islands with a focus", " &f| &7on their fun parkour"), 14), // Islands with parkour
        SHOPS(Material.SPRUCE_SIGN, listOf(" &f| &7Islands with shops for", " &f| &7anyone to buy/sell at."), 15), // Islands with shops
        DESIGN(Material.PINK_TULIP, listOf(" &f| &7Islands that are focused", " &f| &7on their design aesthetic"), 16) // Islands focus on their design and look.
    }
}
