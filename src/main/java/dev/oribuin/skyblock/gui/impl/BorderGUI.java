package dev.oribuin.skyblock.gui.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import dev.oribuin.skyblock.gui.MenuItem;
import dev.oribuin.skyblock.gui.PluginMenu;
import dev.oribuin.skyblock.island.Island;
import dev.oribuin.skyblock.island.member.BorderColor;
import dev.oribuin.skyblock.island.member.Member;
import dev.oribuin.skyblock.manager.DataManager;
import dev.oribuin.skyblock.util.item.ItemConstruct;
import dev.oribuin.skyblock.util.nms.NMSUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BorderGUI extends PluginMenu {

    private final DataManager manager = this.rosePlugin.getManager(DataManager.class);
    private final Map<UUID, BorderColor> activeColors = new HashMap<>();

    public BorderGUI(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    /**
     * Open the border menu for the player
     *
     * @param player The player to open the menu for
     */
    public void open(Player player) {
        Gui gui = this.createGUI(player);
        this.sync(() -> gui.open(player));

        gui.setCloseGuiAction(event -> {
            Member member = this.manager.getMember(player.getUniqueId());
            BorderColor active = this.activeColors.get(player.getUniqueId());

            if (active != null && active != member.getBorder()) {
                member.setBorder(active);
                this.manager.saveMember(member);
                player.sendMessage("updated border to " + active.name().toLowerCase() + " border");

                Island island = this.manager.getIsland(player.getLocation());
                if (island != null) {
                    NMSUtil.sendWorldBorder(player, active, island.getSize(), island.getCenter());
                }
            }
        });

        CommentedConfigurationSection extraItems = this.config.getConfigurationSection("extra-items");
        if (extraItems != null) {
            extraItems.getKeys(false).forEach(s -> MenuItem.create(this.config)
                    .path("extra-items." + s)
                    .player(player)
                    .place(gui));
        }

        this.applyIcons(gui, player);
    }

    /**
     * Apply all the border icons to the gui for the player
     *
     * @param gui    The gui to apply the icons to
     * @param player The player to apply the icons for
     */
    private void applyIcons(Gui gui, Player player) {
        BorderColor active = this.activeColors.getOrDefault(player.getUniqueId(), BorderColor.BLUE);

        for (BorderColor color : BorderColor.values()) {
            ItemConstruct construct = ItemConstruct.deserialize(this.config.getConfigurationSection("border-items." + color.name().toLowerCase()));
            if (construct == null) continue;

            construct.glowing(color == active);
            int slot = this.config.getInt("border-items." + color.name().toLowerCase() + ".slot");
            gui.setItem(slot, new GuiItem(construct.build(), event -> {
                this.activeColors.put(player.getUniqueId(), color);
                this.applyIcons(gui, player);
            }));
        }

        StringPlaceholders current = StringPlaceholders.of("border", active.name().toLowerCase());

        gui.update();
        gui.updateTitle(current.apply(this.config.getString("gui-settings.title", "<missing-title>")));
    }

    /**
     * @return The name of the GUI
     */
    @Override
    public String getMenuName() {
        return "border";
    }

}
