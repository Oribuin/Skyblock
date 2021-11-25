package xyz.oribuin.skyblock.island

import org.bukkit.Location
import java.util.*

data class Island(val key: Int, var owner: UUID, val center: Location) {

    var ownerMember: Member = Member(owner)

    init {
        this.ownerMember.role = Member.Role.OWNER
        this.ownerMember.island = this.key
    }

    var home: Location = center
    var settings = Settings(key)
    var upgrade = Upgrade(key)
    var warp = Warp(key, center)
    var members = mutableListOf(ownerMember)
    var trusted = mutableListOf<UUID>()
    var banned = mutableListOf<UUID>()

}