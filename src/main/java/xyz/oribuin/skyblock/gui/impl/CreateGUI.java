package xyz.oribuin.skyblock.gui.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.skyblock.gui.MenuItem;
import xyz.oribuin.skyblock.gui.PluginMenu;
import xyz.oribuin.skyblock.island.Island;
import xyz.oribuin.skyblock.manager.WorldManager;
import xyz.oribuin.skyblock.util.ItemBuilder;
import xyz.oribuin.skyblock.world.IslandSchematic;

public class CreateGUI extends PluginMenu {

    private final WorldManager manager = this.rosePlugin.getManager(WorldManager.class);

    public CreateGUI(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    public void open(Player player) {
        Gui gui = this.createGUI(player);
        this.sync(() -> gui.open(player));

        CommentedConfigurationSection extraItems = this.config.getConfigurationSection("extra-items");
        if (extraItems != null) {
            extraItems.getKeys(false).forEach(s -> MenuItem.create(this.config)
                    .path("extra-items." + s)
                    .player(player)
                    .place(gui));
        }

        for (IslandSchematic schematic : this.manager.getSchematics().values()) {
            ItemStack stack = new ItemBuilder(schematic.getIcon())
                    .name(HexUtils.colorify(schematic.getDisplayName()))
                    .lore(schematic.getLore().stream().map(HexUtils::colorify).toList())
                    .build();

            gui.addItem(new GuiItem(stack, event -> Island.create(
                    player,
                    schematic,
                    x -> player.closeInventory()
            )));
        }

        gui.update();
    }

    /**
     * @return The name of the GUI
     */
    @Override
    public String getMenuName() {
        return "create";
    }

}
