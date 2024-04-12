package xyz.oribuin.skyblock.gui.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import xyz.oribuin.skyblock.gui.MenuItem;
import xyz.oribuin.skyblock.gui.PluginMenu;
import xyz.oribuin.skyblock.island.Island;

public class PanelGUI extends PluginMenu {

    public PanelGUI(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    public void open(Player player, Island island) {
        Gui gui = this.createGUI(player);
        this.sync(() -> gui.open(player));

        CommentedConfigurationSection extraItems = this.config.getConfigurationSection("extra-items");
        if (extraItems != null) {
            extraItems.getKeys(false).forEach(s -> MenuItem.create(this.config)
                    .path("extra-items." + s)
                    .player(player)
                    .place(gui));
        }

        MenuItem.create(this.config)
                .path("home-item")
                .player(player)
                .action(event -> island.teleport(player))
                .place(gui);

        MenuItem.create(this.config)
                .path("settings-item")
                .player(player)
//                .action(event -> MenuProvider.get(SettingsGUI))
                .place(gui);

        MenuItem.create(this.config)
                .path("warps-item")
                .player(player)
//                .action(event -> MenuProvider.get(WarpsGUI))
                .place(gui);
    }

    /**
     * @return The name of the GUI
     */
    @Override
    public String getMenuName() {
        return "panel";
    }

}
