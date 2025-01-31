package dev.oribuin.skyblock.hook;


import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.oribuin.skyblock.SkyblockPlugin;

public class PAPIProvider extends PlaceholderExpansion {

    private final SkyblockPlugin plugin;

    public PAPIProvider(SkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        return null;

    }

    @Override
    public @NotNull String getIdentifier() {
        return "skyblock";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Oribuin";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

}