package xyz.oribuin.skyblock.gui.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.skyblock.gui.MenuItem;
import xyz.oribuin.skyblock.gui.PluginMenu;
import xyz.oribuin.skyblock.island.Island;
import xyz.oribuin.skyblock.island.IslandBiome;
import xyz.oribuin.skyblock.manager.WorldManager;
import xyz.oribuin.skyblock.util.ItemBuilder;
import xyz.oribuin.skyblock.util.PluginUtil;

import java.util.List;

public class BiomeGUI extends PluginMenu {

    private final WorldManager worldManager = this.rosePlugin.getManager(WorldManager.class);

    public BiomeGUI(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    public void open(Player player, Island island) {
        PaginatedGui gui = this.createPagedGUI(player);
        this.sync(() -> gui.open(player));

        CommentedConfigurationSection extraItems = this.config.getConfigurationSection("extra-items");
        if (extraItems != null) {
            extraItems.getKeys(false).forEach(s -> MenuItem.create(this.config)
                    .path("extra-items." + s)
                    .player(player)
                    .place(gui));
        }

        MenuItem.create(this.config)
                .path("next-page")
                .player(player)
                .action(event -> gui.next())
                .place(gui);

        MenuItem.create(this.config)
                .path("previous-page")
                .player(player)
                .action(event -> gui.previous())
                .place(gui);

        // TODO: Config defined items
        for (IslandBiome biome : this.worldManager.getBiomes().values()) {
            ItemStack item = new ItemBuilder(biome.getIcon())
                    .name(PluginUtil.color("<#a6b2fc><bold>" + biome.getDisplayName()))
                    .lore(List.of(
                            PluginUtil.color(" <white>| <#a6b2fc>Shift-Left Click<gray> to change"),
                            PluginUtil.color(" <white>| <gray>your island to this biome."),
                            PluginUtil.color(" <white>|"),
                            PluginUtil.color(" <white>| <gray>Cost: <#a6b2fc>$" + biome.getCost())
                    ))
                    .build();

            gui.addItem(new GuiItem(item, event -> {
                biome.apply(island);
                player.sendMessage(PluginUtil.color("&aYou have changed your island biome to " + biome.getDisplayName()));
            }));
        }

        gui.update();
    }

    /**
     * @return The name of the GUI
     */
    @Override
    public String getMenuName() {
        return "biome";
    }

}
