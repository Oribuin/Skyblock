package xyz.golimc.skyblock.island

import org.bukkit.Location
import java.util.*

data class Island(val key: Int, var owner: UUID, val center: Location) {

    var ownerMember: Member = Member(owner)

    init {
        this.ownerMember.role = Member.Role.OWNER
        this.ownerMember.island = this.key
    }

    var settings = Settings(key)
    var upgrades = Upgrades(key)
    var warp = Warp(key, center)
    var members = mutableListOf(ownerMember)
    var trusted = mutableListOf<UUID>()
}