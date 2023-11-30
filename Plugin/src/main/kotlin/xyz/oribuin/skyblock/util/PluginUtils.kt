package xyz.oribuin.skyblock.util

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.config.CommentedConfigurationSection
import dev.rosewood.rosegarden.manager.Manager
import dev.rosewood.rosegarden.utils.HexUtils
import dev.rosewood.rosegarden.utils.NMSUtil
import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.apache.commons.lang3.StringUtils
import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.gui.PluginGUI
import xyz.oribuin.skyblock.hook.PAPI
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.manager.DataManager
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.manager.LocaleManager
import xyz.oribuin.skyblock.manager.MenuManager
import java.io.File
import java.nio.file.Files
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.reflect.KClass


inline fun <reified T : Manager> RosePlugin.getManager(): T = this.getManager(T::class.java)

fun RosePlugin.send(
    receiver: CommandSender,
    messageId: String,
    placeholders: StringPlaceholders = StringPlaceholders.empty()
) {
    this.getManager<LocaleManager>().sendMessage(receiver, messageId, placeholders)
}

fun RosePlugin.copyResourceTo(resourcePath: String, output: File) {
    output.parentFile.mkdirs();

    val resource = this.getResource(resourcePath)
    requireNotNull(resource) { "Resource not found: $resourcePath" }

    Files.copy(resource, output.toPath())

}

fun String.color(): String = HexUtils.colorify(this)

fun List<String>.color(): List<String> = this.map { HexUtils.colorify(it) }

/**
 * Get a bukkit color from a hex code
 *
 * @return The bukkit color
 */
fun String?.toColor(): Color {
    this ?: return Color.BLACK

    val color: java.awt.Color = try {
        java.awt.Color.decode(this)
    } catch (ex: NumberFormatException) {
        return Color.BLACK
    }
    return Color.fromRGB(color.red, color.green, color.blue)
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


fun <T : Enum<T>> next(enum: T): T {
    val values = enum.javaClass.enumConstants

    // if the enum is the last one, return the first one
    if (enum.ordinal == values.size - 1)
        return values[0]

    // return the next enum
    return values[enum.ordinal + 1]
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
        return java.lang.Enum.valueOf(enum.java, value.uppercase())
    } catch (ex: Exception) {
        error("Invalid ${enum.simpleName} specified: $value")
    }
}

fun numRange(start: Int, end: Int): List<Int> {
    val list = mutableListOf<Int>()
    for (i in start..end)
        list.add(i)

    return list
}



/**
 * Deserialize an ItemStack from a CommentedConfigurationSection with placeholders
 *
 * @param section      The section to deserialize from
 * @param sender       The CommandSender to apply placeholders from
 * @param key          The key to deserialize from
 * @param placeholders The placeholders to apply
 * @return The deserialized ItemStack
 */
fun deserialize(
    section: CommentedConfigurationSection,
    sender: CommandSender? = null,
    key: String,
    placeholders: StringPlaceholders = StringPlaceholders.empty()
): ItemStack? {
    val locale = SkyblockPlugin.instance.getManager(LocaleManager::class.java)
    val material = Material.getMaterial(section.getString("$key.material") ?: "") ?: return null

    // Load enchantments
    val enchantments = mutableMapOf<Enchantment, Int>()
    val enchantmentSection = section.getConfigurationSection("$key.enchantments")
    if (enchantmentSection != null) {
        for (enchantmentKey in enchantmentSection.getKeys(false)) {
            val enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentKey.lowercase(Locale.getDefault()))) ?: continue
            enchantments[enchantment] = enchantmentSection.getInt(enchantmentKey, 1)
        }
    }

    // Load potion item flags
    val flags = section.getStringList("$key.flags").stream()
        .filter { it != null }
        .map { ItemFlag.valueOf(it) }
        .toArray<ItemFlag> { arrayOf() }

    // Get the player head owner
    val owner = section.getString("$key.owner")
    var offlinePlayer: OfflinePlayer? = null

    if (owner != null) {
        offlinePlayer = if (owner.equals("self", true) && sender is OfflinePlayer) {
            sender
        } else {
            Bukkit.getOfflinePlayerIfCached(owner)
        }
    }

    return ItemBuilder(material)
        .name(locale.format(sender, section.getString("$key.name"), placeholders))
        .amount(min(1, section.getInt("$key.amount", 1)))
        .lore(locale.format(sender, section.getStringList("$key.lore"), placeholders))
        .glow(section.getBoolean("$key.glowing", false))
        .unbreakable(section.getBoolean("$key.unbreakable", false))
        .model(locale.format(sender, section.getString("$key.model-data"), placeholders)?.toIntOrNull() ?: -1)
        .enchant(enchantments)
        .flags(flags)
        .owner(offlinePlayer)
        .texture(locale.format(sender, section.getString("$key.texture"), placeholders))
        .build()
}

/**
 * Format a string with placeholders and color codes
 *
 * @param player The player to format the string for
 * @param text   The string to format
 * @return The formatted string
 */
fun format(player: Player?, text: String): String {
    return format(player, text, StringPlaceholders.empty())
}

/**
 * Format a string with placeholders and color codes
 *
 * @param player       The player to format the string for
 * @param text         The text to format
 * @param placeholders The placeholders to replace
 * @return The formatted string
 */
fun format(player: Player?, text: String, placeholders: StringPlaceholders?): String {
    return (placeholders?.apply(text)?.let { PAPI.apply(player, it) })?.color() ?: text
}

/**
 * A simple method to get a player as a member
 *
 * @param player The player to get
 * @return The member
 */
fun Player.asMember(rosePlugin: RosePlugin): Member = rosePlugin.getManager<IslandManager>().getMember(this)


/**
 * Get the player's island
 *
 * @param player The player
 * @return The island
 */
fun Player.getIsland(rosePlugin: RosePlugin): Island? = rosePlugin.getManager<IslandManager>().getIsland(this)

/**
 * Get a member's island
 * @param member The member
 * @return The island
 */
fun Member.getIsland(rosePlugin: RosePlugin): Island? = rosePlugin.getManager<IslandManager>().getIsland(this)

fun Island.cache(rosePlugin: RosePlugin) = rosePlugin.getManager<DataManager>().cacheIsland(this)

fun <T : PluginGUI> RosePlugin.getMenu(kclass: KClass<T>): T = this.getManager<MenuManager>()[kclass]

fun CommandContext.asPlayer(): Player = this.sender as Player

fun CommandContext.asMember(rosePlugin: RosePlugin): Member = (this.sender as Player).asMember(rosePlugin)

fun Enum<*>.format(): String = StringUtils.capitalize(this.name.lowercase().replace("_", " "))

fun String.formatEnum(): String = StringUtils.capitalize(this.lowercase().replace("_", " "))

fun Location.format(): String = "${this.blockX}, ${this.blockY}, ${this.blockZ}"