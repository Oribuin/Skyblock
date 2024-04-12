package xyz.oribuin.skyblock.gui.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.skyblock.gui.MenuItem;
import xyz.oribuin.skyblock.gui.PluginMenu;
import xyz.oribuin.skyblock.island.Island;
import xyz.oribuin.skyblock.island.member.BorderColor;
import xyz.oribuin.skyblock.island.member.Member;
import xyz.oribuin.skyblock.manager.DataManager;
import xyz.oribuin.skyblock.util.ItemBuilder;
import xyz.oribuin.skyblock.util.SkyblockUtil;
import xyz.oribuin.skyblock.util.nms.NMSUtil;

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
            ItemStack itemStack = SkyblockUtil.deserialize(this.config, player, color.name().toLowerCase() + "-border");
            if (itemStack == null) return;

            itemStack = new ItemBuilder(itemStack).glow(color == active).build();
            int slot = this.config.getInt("border-items." + color.name().toLowerCase() + ".slot");
            gui.setItem(slot, new GuiItem(itemStack, event -> {
                this.activeColors.put(player.getUniqueId(), color);
                this.applyIcons(gui, player);
            }));
        }

        StringPlaceholders current = StringPlaceholders.of("border", active.name().toLowerCase());

        gui.update();
        gui.updateTitle(HexUtils.colorify(current.apply(this.config.getString("gui-settings.title"))));
    }

    /**
     * @return The name of the GUI
     */
    @Override
    public String getMenuName() {
        return "border";
    }

}
