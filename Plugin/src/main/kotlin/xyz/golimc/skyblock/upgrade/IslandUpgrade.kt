package xyz.golimc.skyblock.upgrade

import org.bukkit.Material

class IslandUpgrade(type: Type) {

    val tiers = mutableMapOf<Int, Tier>()

    // Display icon in the gui.
    var displayName: String = type.name.lowercase().replaceFirstChar { it.uppercase() }
    var icon: Material = Material.BEACON
    var lore = mutableListOf<String>()

    data class Tier(
        val cost: Double,
        val value: Any
    )

    enum class Type {
        SIZE, CHESTGEN, FLY
    }

}