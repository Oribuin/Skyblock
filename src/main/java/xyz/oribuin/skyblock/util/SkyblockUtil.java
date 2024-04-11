package xyz.oribuin.skyblock.util;

import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class SkyblockUtil {

    /**
     * Get an enum from a string value
     *
     * @param enumClass The enum class
     * @param name      The name of the enum
     * @param <T>       The enum type
     * @return The enum
     */
    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name) {
        if (name == null)
            return null;

        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        return null;
    }

    /**
     * Get an enum from a string value
     *
     * @param enumClass The enum class
     * @param name      The name of the enum
     * @param def       The default enum
     * @param <T>       The enum type
     * @return The enum
     */
    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name, T def) {
        if (name == null)
            return def;

        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        return def;
    }

    /**
     * Create a file in a folder from the plugin's resources
     *
     * @param rosePlugin The plugin
     * @param folders    The folders
     * @return The file
     */
    @NotNull
    public static File createFile(@NotNull RosePlugin rosePlugin, @NotNull String... folders) {
        File file = new File(rosePlugin.getDataFolder(), String.join("/", folders)); // Create the file
        if (file.exists())
            return file;

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        String path = String.join("/", folders);
        try (InputStream stream = rosePlugin.getResource(path)) {
            if (stream == null) {
                file.createNewFile();
                return file;
            }

            Files.copy(stream, Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static Location getNextIslandLocation(int locationId, World islandWorld) {
        if (locationId == 0)
            return new Location(islandWorld, 0.0, 65, 0.0);

        int n = locationId - 1;
        double r = Math.floor((Math.sqrt(n + 1.0) - 1) / 2) + 1;
        double p = (8 * r * (r - 1)) / 2;
        double en = r * 2;
        double a = (1 + n - p) % (r * 8);

        double x = 0.0;
        double z = 0.0;
        switch ((int) Math.floor(a / (r * 2))) {
            case 0 -> {
                x = a - r;
                z = -r;
            }
            case 1 -> {
                x = r;
                z = (a % en) - r;
            }
            case 2 -> {
                x = r - (a % en);
                z = r;
            }
            case 3 -> {
                x = -r;
                z = r - (a % en);
            }
        }

        return new Location(islandWorld, x * 1200, 65, z * 1200);
    }

}
