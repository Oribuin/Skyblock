package xyz.golimc.skyblock.island

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import xyz.golimc.skyblock.nms.BorderColor
import java.util.*

data class Member(val uuid: UUID) {
    var island: Int = -1 // The Island ID
    var role = Role.MEMBER // The Role of the island member
    var border: BorderColor = BorderColor.BLUE // The color of the player's unique border color.

    enum class Role(val priority: Int) { // Island Roles and their priority
        OWNER(0),
        ADMIN(1),
        MEMBER(2)
    }

    // Does the user have an island.
    val hasIsland: Boolean
        get() = island >= 1

    // Get the member as a player.
    val offlinePlayer: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(this.uuid)
}
