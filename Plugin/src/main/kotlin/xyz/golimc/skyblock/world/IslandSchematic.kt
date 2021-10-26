package xyz.golimc.skyblock.world

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.session.ClipboardHolder
import org.bukkit.Location
import org.bukkit.Material
import xyz.golimc.skyblock.SkyblockPlugin
import java.io.File
import java.io.FileInputStream

/**
 * @author Esophose
 */
class IslandSchematic(
    val name: String,
    private val file: File,
    val displayName: String,
    val icon: Material,
    val lore: MutableList<String>
) {
    private val format = ClipboardFormats.findByFile(this.file) ?: error("${this.name} is not a valid schematic.")

    /**
     * Paste a schematic into the world
     *
     * @param plugin The main class
     * @param location The location of the schematic
     * @param callback The callback function for the paste task.
     */
    fun paste(plugin: SkyblockPlugin, location: Location, callback: (() -> Unit)? = null) {
        val clipboard: Clipboard
        this.format.getReader(FileInputStream(this.file)).use { clipboard = it.read() }

        val task = Runnable {
            WorldEdit.getInstance().newEditSessionBuilder()
                .world(BukkitAdapter.adapt(location.world))
                .maxBlocks(-1)
                .build().use {
                    Operations.complete(
                        ClipboardHolder(clipboard).createPaste(it)
                            .to(BukkitAdapter.asBlockVector(location))
                            .copyEntities(true)
                            .ignoreAirBlocks(true)
                            .build()
                    )
                }

            callback?.invoke()
        }

        if (plugin.server.pluginManager.isPluginEnabled("FastAsyncWorldEdit") || plugin.server.pluginManager.isPluginEnabled("AsyncWorldEdit"))
            plugin.server.scheduler.runTaskAsynchronously(plugin, task)
        else
            task.run()
    }
}