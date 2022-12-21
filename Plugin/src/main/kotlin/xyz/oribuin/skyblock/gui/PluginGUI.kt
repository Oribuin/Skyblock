package xyz.oribuin.skyblock.gui

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.config.CommentedFileConfiguration
import dev.rosewood.rosegarden.utils.StringPlaceholders
import dev.triumphteam.gui.guis.BaseGui
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import dev.triumphteam.gui.guis.PaginatedGui
import java.io.File
import java.util.function.Consumer
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import xyz.oribuin.skyblock.hook.PAPI
import xyz.oribuin.skyblock.util.color
import xyz.oribuin.skyblock.util.getItemStack

abstract class PluginGUI(protected val rosePlugin: RosePlugin) {

    protected lateinit var config: CommentedFileConfiguration

    /**
     * Get the default config values for the GUI
     *
     * @return The default config values
     */
    protected abstract val defaultValues: Map<String, Any>

    /**
     * @return The name of the GUI
     */
    protected abstract val menuName: String

    /**
     * Get the config values for the GUI
     *
     * @param path The path to the config values
     * @param def  The default value if the path is not found
     * @param <T>  The type of the config value
    </T> */
    @Suppress("UNCHECKED_CAST")
    protected fun <T : Any> get(path: String, def: T? = null): T {
        return (this.config.get(path, def) ?: def) as T
    }

    /**
     * Create the menu file if it doesn't exist and add the default values
     */
    fun load() {
        val folder = File(rosePlugin.dataFolder, "menus")
        var newFile = false
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder, "$menuName.yml")
        if (!file.exists()) {
            file.createNewFile()
            newFile = true
        }

        config = CommentedFileConfiguration.loadConfiguration(file)

        if (newFile) {
            defaultValues.forEach { (path: String, `object`: Any) ->
                if (path.startsWith("#"))
                    config.addPathedComments(path, `object`.toString())
                else
                    config.set(path, `object`)
            }
        }

        config.save(file)
    }

    /**
     * Create a paged GUI for the given player
     *
     * @param player The player to create the GUI for
     * @return The created GUI
     */
    protected fun createPagedGUI(player: Player): PaginatedGui {
        return Gui.paginated()
            .rows(this.get("gui-settings.rows", 6))
            .title(this.format(player, this.get("gui-settings.title", menuName)))
            .disableAllInteractions()
            .create()
    }

    /**
     * Create a GUI for the given player without pages
     *
     * @param player The player to create the GUI for
     * @return The created GUI
     */
    protected fun createGUI(player: Player): Gui {
        return Gui.gui()
            .rows(this.get("gui-settings.rows", 6))
            .title(this.format(player, this.get("gui-settings.title", menuName)))
            .disableAllInteractions()
            .create()
    }

    /**
     * Place an empty item in the gui.
     *
     * @param gui  The GUI
     * @param slot The Item Slot
     * @param item The Item
     */
    protected fun put(gui: BaseGui, slot: Int, item: ItemStack) {
        gui.setItem(slot, GuiItem(item))
    }

    /**
     * A cleaner function for setting an item in the gui.
     *
     * @param gui      The GUI
     * @param itemPath The path to the item
     * @param player   The item viewer
     */
    protected fun put(gui: BaseGui, itemPath: String, player: Player) {
        this.put(gui, itemPath, player, StringPlaceholders.empty())
    }

    /**
     * A cleaner function for setting an item in the gui.
     *
     * @param gui           The GUI
     * @param itemPath      The path to the item
     * @param viewer        The item viewer
     * @param eventConsumer The event consumer
     */
    protected fun put(gui: BaseGui, itemPath: String, viewer: Player, eventConsumer: Consumer<InventoryClickEvent>) {
        this.put(gui, itemPath, viewer, StringPlaceholders.empty(), eventConsumer)
    }

    /**
     * A cleaner function for setting an item in the gui.
     *
     * @param gui           The GUI
     * @param itemPath      The path to the item
     * @param viewer        The item viewer
     * @param placeholders  The placeholders to use
     * @param eventConsumer The event consumer
     */
    protected fun put(gui: BaseGui, itemPath: String, viewer: Player, placeholders: StringPlaceholders, eventConsumer: Consumer<InventoryClickEvent> = Consumer { }) {
        if (!this.get("$itemPath.enabled", true))
            return

        val slot = this.config.get("$itemPath.slot")
        if (slot != null && slot is Int) {
            this.put(gui, slot, itemPath, viewer, placeholders, eventConsumer)
            return
        }

        val slots = this.config.getList("$itemPath.slots")
        if (slots != null) {
            slots.forEach { listSlot ->
                if (listSlot is Int) {
                    this.put(gui, listSlot, itemPath, viewer, placeholders, eventConsumer)
                }

                if (listSlot is String)
                    this.put(gui, this.parseStringToSlots(listSlot), itemPath, viewer, placeholders, eventConsumer)
            }
            return
        }

    }

    protected fun put(
        gui: BaseGui,
        slot: Int,
        itemPath: String,
        viewer: Player,
        placeholders: StringPlaceholders = StringPlaceholders.empty(),
        eventConsumer: Consumer<InventoryClickEvent> = Consumer { }
    ) {
        this.put(gui, listOf(slot), itemPath, viewer, placeholders, eventConsumer)
    }

    /**
     * A messy function for setting multiple items in the gui.
     *
     * @param gui           The GUI
     * @param slots         The slots of the items
     * @param itemPath      The path to the item
     * @param viewer        The item viewer
     * @param placeholders  The placeholders to use
     * @param eventConsumer The event consumer
     */
    protected fun put(
        gui: BaseGui,
        slots: List<Int>,
        itemPath: String,
        viewer: Player,
        placeholders: StringPlaceholders = StringPlaceholders.empty(),
        eventConsumer: Consumer<InventoryClickEvent> = Consumer { }
    ) {

        val item = getItemStack(config, itemPath, viewer, placeholders)
        gui.setItem(slots, GuiItem(item) { t -> eventConsumer.accept(t) })
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player       The player to format the string for
     * @param text         The text to format
     * @param placeholders The placeholders to replace
     * @return The formatted string
     */
    private fun format(player: Player, text: String, placeholders: StringPlaceholders = StringPlaceholders.empty()): Component {
        return Component.text(PAPI.apply(player, placeholders.apply(text)).color())
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player       The player to format the string protected
     * @param text         The text to format
     * @param placeholders The placeholders to replace
     * @return The formatted string
     */
    protected fun formatString(player: Player, text: String, placeholders: StringPlaceholders = StringPlaceholders.empty()): String {
        return PAPI.apply(player, placeholders.apply(text)).color()
    }

    private fun parseStringToSlots(string: String): List<Int> {
        val split = string.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (split.size != 2) {
            try {
                return listOf(string.toInt())
            } catch (ignored: NumberFormatException) {
            }
        }
        return getNumberRange(split[0].toInt(), split[1].toInt())
    }

    /**
     * Get a range of numbers as a list
     *
     * @param start The start of the range
     * @param end   The end of the range
     * @return A list of numbers
     */
    protected fun getNumberRange(start: Int, end: Int): List<Int> {
        if (start == end) {
            return listOf(start)
        }
        val list = mutableListOf<Int>()
        for (i in start..end) {
            list.add(i)
        }
        return list
    }

    /**
     * Get the page placeholders for the gui
     *
     * @param gui The gui
     * @return The page placeholders
     */
    protected fun getPagePlaceholders(gui: PaginatedGui): StringPlaceholders {
        return StringPlaceholders.builder()
            .addPlaceholder("page", gui.currentPageNum)
            .addPlaceholder("total", gui.pagesNum.coerceAtLeast(1))
            .addPlaceholder("next", gui.nextPageNum)
            .addPlaceholder("previous", gui.prevPageNum)
            .build()
    }

    // Create extension function from a basegui using <T : BaseGui>

    /**
     * Add extra items to the gui
     *
     * @param gui The gui
     * @param player The viewer
     */
    protected fun addExtraItems(gui: BaseGui, player: Player) {
        val extraItems = config.getConfigurationSection("extra-items") ?: return

        for (key in extraItems.getKeys(false)) {
            this.put(gui, "extra-items.$key", player)
        }

    }

    fun async(runnable: Runnable) = this.rosePlugin.server.scheduler.runTaskAsynchronously(this.rosePlugin, runnable)
}