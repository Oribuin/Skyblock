package xyz.oribuin.skyblock.util

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import xyz.oribuin.orilibrary.OriPlugin
import xyz.oribuin.orilibrary.manager.Manager
import xyz.oribuin.orilibrary.util.HexUtils
import xyz.oribuin.orilibrary.util.StringPlaceholders
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.manager.MessageManager
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.reflect.KClass


inline fun <reified T : Manager> OriPlugin.getManager(): T = this.getManager(T::class.java)

fun SkyblockPlugin.send(receiver: CommandSender, messageId: String, placeholders: StringPlaceholders = StringPlaceholders.empty()) {
    this.getManager<MessageManager>().send(receiver, messageId, placeholders)
}

fun String.color(): String = HexUtils.colorify(this)

/**
 * Format a string list into a single string.
 *
 * @return the converted string.
 */
fun List<String>.format(): String {
    val builder = StringBuilder()
    this.forEach { s -> builder.append("$s\n") }
    return builder.toString()
}

/**
 * Format a location into a readable String.
 *
 * @return The formatted Location.
 */
fun Location.format(): String {
    return this.blockX.toString() + ", " + this.blockY + ", " + this.blockZ
}

/**
 * Get the block location of the location.;
 *
 * @return The block location
 */
fun Location.block(): Location {
    return Location(this.world, this.blockX.toDouble(), this.blockY.toDouble(), this.blockZ.toDouble())
}

/**
 * Get a bukkit color from a hex code
 *
 * @return The bukkit color
 */
fun String.toColor(): Color {
    val color: java.awt.Color = try {
        java.awt.Color.decode(this)
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
    return numRange(0, 35).stream().map { player.inventory.getItem(it) }
        .filter { itemStack -> itemStack == null || itemStack.type == Material.AIR }
        .count()
        .toInt()
}

/**
 * Gets a location as a string key
 *
 * @return the location as a string key
 * @author Esophose
 */
fun Location.asKey(): String {
    return String.format("%s-%.2f-%.2f-%.2f", this.world?.name, this.x, this.y, this.z)
}

/**
 * @author  https://stackoverflow.com/a/19287714
 * Code happily stolen from Esophose
 */
fun getNextIslandLocation(locationId: Int, world: World?, islandDistance: Int): Location {
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

    return Location(world, x * islandDistance, 65.0, z * islandDistance, 180f, 0f)
}

/**
 * Parse an enum or error
 * @param enum The enum
 * @param value The name of the enum
 *
 * @return The enum if found.
 */
fun <T : Enum<T>> parseEnum(enum: KClass<T>, value: String): T {
    try {
        return enum.java.enumConstants.first { it.name.equals(value, true) } ?: error("")
    } catch (ex: Exception) {
        error("Invalid ${enum.simpleName} specified: $value")
    }

}

/**
 * Check if the server is using Paper
 *
 * @return True if the server can find paper.
 */
val usingPaper: Boolean
    get() = try {
        Class.forName("com.destroystokyo.paper.util.VersionFetcher")
        true
    } catch (ex: ClassNotFoundException) {
        false
    }


fun numRange(start: Int, end: Int): List<Int> {
    val list = mutableListOf<Int>()
    for (i in start..end)
        list.add(i)

    return list
}

fun Location.center() = Location(this.world, this.blockX + 0.5, this.blockY + 0.0, this.blockZ + 0.5)