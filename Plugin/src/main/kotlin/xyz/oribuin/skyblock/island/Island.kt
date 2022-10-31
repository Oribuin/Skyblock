package xyz.oribuin.skyblock.island

import java.util.*
import org.bukkit.Location

data class Island(val key: Int, var owner: UUID, val center: Location) {

    var ownerMember: Member = Member(owner)
    var home: Location = center
    var settings = Settings(key)
    var warp = Warp(key, center)
    var members = mutableListOf(ownerMember)
    var trusted = mutableListOf<UUID>()
    var lastSave: Long = 0

    init {
        this.ownerMember.role = Member.Role.OWNER
        this.ownerMember.island = this.key
    }

}