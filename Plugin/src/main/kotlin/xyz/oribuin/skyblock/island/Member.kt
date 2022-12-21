package xyz.oribuin.skyblock.island

import java.util.*
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.nms.BorderColor

data class Member(val uuid: UUID) {
    var username: String = "Unknown" // The username of the player
    var island: Int = -1 // The Island ID
    var role = Role.MEMBER // The Role of the island member
    var border: BorderColor = BorderColor.BLUE // The color of the player's unique border color.

    enum class Role(val priority: Int) { // Island Roles and their priority
        OWNER(0),
        ADMIN(1),
        MEMBER(2),
        NONE(3)
    }

    // Does the user have an island.
    val hasIsland: Boolean
        get() = island >= 1

    // Get the member as a player.
    val offlinePlayer: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(this.uuid)

    val onlinePlayer: Player?
        get() = this.offlinePlayer.player

}
