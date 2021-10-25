package xyz.golimc.skyblock.island

import org.bukkit.Location
import java.util.*

data class Island(val key: Int, var owner: UUID, val center: Location) {
    var settings = Settings(key)
    var upgrades = Upgrades(key)
    var warp = Warp(key)
    var members = mutableListOf<Member>()
    var trusted = mutableListOf<UUID>()
}