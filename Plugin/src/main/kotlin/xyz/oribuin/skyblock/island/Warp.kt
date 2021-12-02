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
    var public: Boolean = false // Whether the island is open to the public or not.

    // A way to prevent people insta farming votes & visits constantly without requiring a database.
    val votedUsers = mutableListOf<UUID>() // Cache users who have upvoted the island
    val visitUsers = mutableListOf<UUID>() // Cache the users who have tp'd to the island already

    data class Desc(val text: MutableList<String> = mutableListOf("This warp doesn't have a description."))

    enum class Category(val icon: Material, val desc: Array<String>) {
        GENERAL(Material.NAME_TAG, arrayOf("Miscellaneous island with multiple purposes!")), // A vague island category
        FARMS(Material.DIAMOND_HOE, arrayOf("Islands that have public farms.")), // Islands dedicated to farming, XP, Crops, Mob Loot, Etc
        PARKOUR(Material.FEATHER, arrayOf("Islands with a focus on their fun parkour")), // Islands with parkour
        SHOPS(Material.SPRUCE_SIGN, arrayOf("Islands that are buying/selling in shops ")), // Islands with shops
        DESIGN(Material.PINK_TULIP, arrayOf("Islands that are focused", "on their design aesthetic")) // Islands focus on their design and look.
    }
}
