package xyz.oribuin.skyblock.island.member;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import xyz.oribuin.skyblock.nms.BorderColor;

import java.util.UUID;

public class Member {

    private final UUID uuid;
    private String username;
    private int island;
    private Role role;
    private BorderColor border;

    /**
     * Create a new instanceof Member with a UUID
     *
     * @param uuid The UUID of the player
     */
    public Member(UUID uuid) {
        this.uuid = uuid;
        this.username = "Unknown";
        this.island = -1;
        this.role = Role.MEMBER;
        this.border = BorderColor.BLUE;
    }

    /**
     * Get the player as an offline player
     *
     * @return The player object
     */
    public OfflinePlayer getOffline() {
        return Bukkit.getOfflinePlayer(this.uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIsland() {
        return island;
    }

    public void setIsland(int island) {
        this.island = island;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public BorderColor getBorder() {
        return border;
    }

    public void setBorder(BorderColor border) {
        this.border = border;
    }

}