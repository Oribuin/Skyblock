package xyz.golimc.skyblock.util

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import xyz.oribuin.orilibrary.OriPlugin
import xyz.oribuin.orilibrary.manager.Manager
import kotlin.math.floor
import kotlin.math.sqrt

inline fun <reified T : Manager> OriPlugin.getManager(): T = this.getManager(T::class.java)

/**
 * Format a string list into a single string.
 *
 * @param stringList The strings being converted
 * @return the converted string.
 */
fun formatList(stringList: List<String>): String {
    val builder = StringBuilder()
    stringList.forEach { s -> builder.append(s).append("\n") }
    return builder.toString()
}

/**
 * Format a location into a readable String.
 *
 * @param loc The location
 * @return The formatted Location.
 */
fun formatLocation(loc: Location?): String {
    return if (loc == null) "None" else loc.blockX.toString() + ", " + loc.blockY + ", " + loc.blockZ
}

/**
 * Get the block location of the location.;
 *
 * @param loc The location;
 * @return The block location
 */
fun getBlockLoc(loc: Location): Location {
    val location = loc.clone()
    return Location(location.world, location.blockX.toDouble(), loc.blockY.toDouble(), loc.blockZ.toDouble())
}

/**
 * Center a location to the center of the block.
 *
 * @param location The location to be centered.
 * @return The centered location.
 */
fun centerLocation(location: Location): Location? {
    val loc = location.clone()
    loc.add(0.5, 0.5, 0.5)
    loc.yaw = 180f
    loc.pitch = 0f
    return loc
}

/**
 * Get a bukkit color from a hex code
 *
 * @param hex The hex code
 * @return The bukkit color
 */
fun fromHex(hex: String?): Color {

    if (hex == null) return Color.BLACK
    val color: java.awt.Color = try {
        java.awt.Color.decode(hex)
    } catch (ex: NumberFormatException) {
        return Color.BLACK
    }
    return Color.fromRGB(color.red, color.green, color.blue)
}

/**
 * Get a configuration value or default from the file config
 *
 * @param config The configuration file.
 * @param path   The path to the value
 * @param def    The default value if the original value doesnt exist
 * @return The config value or default value.
 */
@Suppress("UNCHECKED_CAST")
fun <T> get(config: FileConfiguration, path: String, def: T): T {
    return (config.get(path) as T) ?: def
}

/**
 * Get a value from a configuration section.
 *
 * @param section The configuration section
 * @param path    The path to the option.
 * @param def     The default value for the option.
 * @return The config option or the default.
 */
@Suppress("UNCHECKED_CAST")
fun <T> get(section: ConfigurationSection, path: String, def: T): T {
    return (section.get(path) as T) ?: def
}

/**
 * Get the total number of spare slots in a player's inventory
 *
 * @param player The player
 * @return The amount of empty slots.
 */
fun getSpareSlots(player: Player): Int {

    val slots = mutableListOf<Int>()
    for (i in 0..35) slots.add(i)
    return slots.stream().map { player.inventory.getItem(it) }
        .filter { itemStack -> itemStack == null || itemStack.type == Material.AIR }
        .count()
        .toInt()
}

/**
 * Gets a location as a string key
 *
 * @param location The location
 * @return the location as a string key
 * @author Esophose
 */
fun locationAsKey(location: Location): String {
    return String.format("%s-%.2f-%.2f-%.2f", location.world!!.name, location.x, location.y, location.z)
}

/**
 * @author  https://stackoverflow.com/a/19287714
 * Code happily stolen from Esophose
 */
fun getNextIslandLocation(locationId: Int, world: World?): Location {
    if (locationId == 0)
        return Location(world, 0.0, 65.0, 0.0)

    val n = locationId - 1
    val r = floor((sqrt(n + 1.0) - 1) / 2) + 1
    val p = (8 * r * (r - 1)) / 2
    val en = r * 2
    val a = (1 + n - p) % (r * 8)

    var x = 0.0
    var z = 0.0

    when (floor(a / (r * 2)).toInt()) {
        0 -> {
            x = a - r
            z = -r
        }
        1 -> {
            x = r
            z = (a % en) - r
        }
        2 -> {
            x = r - (a % en)
            z = r
        }
        3 -> {
            x = -r
            z = r - (a % en)
        }
    }

    return Location(world, x * 350, 65.0, z * 350, 180f, 0f)
}