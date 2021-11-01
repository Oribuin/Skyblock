package xyz.oribuin.skyblock.island

class Upgrade(val key: Int) {
    // Normal Upgrades.
    var islandFly: Boolean = false

    // Tiered Upgrades
    var sizeTier: Int = 0
    var chestGenTier: Int = 0
}