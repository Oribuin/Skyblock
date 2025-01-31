package dev.oribuin.skyblock.gui.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.oribuin.skyblock.gui.MenuItem;
import dev.oribuin.skyblock.gui.PluginMenu;
import dev.oribuin.skyblock.island.Island;
import dev.oribuin.skyblock.manager.WorldManager;
import dev.oribuin.skyblock.util.item.ItemConstruct;
import dev.oribuin.skyblock.world.IslandSchematic;

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
            ItemStack stack = ItemConstruct.of(schematic.getIcon())
                    .name(schematic.getDisplayName())
                    .lore(schematic.getLore())
                    .additionalTooltip(false)
                    .build();

            gui.addItem(new GuiItem(stack, event -> Island.create(
                    player,
                    schematic,
                    x -> this.sync(player::closeInventory)
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
