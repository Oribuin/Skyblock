package dev.oribuin.skyblock.world;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import dev.oribuin.skyblock.SkyblockPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class IslandSchematic {

    private final File file;
    private final String name;
    private final String displayName;
    private final Material icon;
    private final List<String> lore;
    private final ClipboardFormat format;

    public IslandSchematic(File file, String name, String displayName, Material icon, List<String> lore) {
        this.file = file;
        this.name = name;
        this.displayName = displayName;
        this.icon = icon;
        this.lore = lore;
        this.format = ClipboardFormats.findByFile(this.file);
    }

    /**
     * Paste a schematic into the world
     *
     * @param plugin   The main class
     * @param location The location of the schematic
     * @param callback The callback function for the paste task.
     */
    public void paste(RosePlugin plugin, Location location, Runnable callback) {
        Clipboard clipboard;
        try (FileInputStream stream = new FileInputStream(this.file)) {
            clipboard = this.format.getReader(stream).read();
        } catch (Exception e) {
            SkyblockPlugin.get().getLogger().severe("Failed to load schematic " + this.name + " at " + location + ": " + e.getMessage());
            return;
        }

        Runnable task = () -> {
            try (EditSession session = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(location.getWorld())).maxBlocks(-1).build()) {
                Operations.complete(
                        new ClipboardHolder(clipboard).createPaste(session)
                                .to(BukkitAdapter.asBlockVector(location))
                                .copyEntities(true)
                                .ignoreAirBlocks(true)
                                .build()
                );

                if (callback != null)
                    callback.run();
            } catch (WorldEditException e) {
                SkyblockPlugin.get().getLogger().severe("Failed to paste schematic " + this.name + " at " + location + ": " + e.getMessage());
            }

        };

        if (plugin.getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit"))
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
        else
            task.run();
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public List<String> getLore() {
        return lore;
    }

    public ClipboardFormat getFormat() {
        return format;
    }

}