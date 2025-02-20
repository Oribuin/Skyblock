package dev.oribuin.skyblock.gui.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import dev.oribuin.skyblock.gui.MenuItem;
import dev.oribuin.skyblock.gui.MenuProvider;
import dev.oribuin.skyblock.gui.PluginMenu;
import dev.oribuin.skyblock.island.Island;

public class SettingsGUI extends PluginMenu {

    public SettingsGUI(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    /**
     * Open the GUI for the player.
     *
     * @param player The player to open the GUI for.
     */
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
                .path("go-back")
                .player(player)
                .action(event -> MenuProvider.get(PanelGUI.class).open(player, island))
                .place(gui);

        gui.update();
    }

    private void applyItems(Gui gui, Player player, Island island) {
        MenuItem.create(this.config)
                .path("island-name")
                .player(player)
                .place(gui);

        MenuItem.create(this.config)
                .path("island-public")
                .player(player)
                .place(gui);

        MenuItem.create(this.config)
                .path("island-animals")
                .player(player)
                .place(gui);

        MenuItem.create(this.config)
                .path("island-mobs")
                .player(player)
                .place(gui);
    }

    /**
     * @return The name of the GUI
     */
    @Override
    public String getMenuName() {
        return "settings";
    }


    //     private val cooldown = mutableMapOf < UUID, Long>()
    //    private val manager = this.rosePlugin.getManager < skyblock.manager.IslandManager > ()
    //
    //    fun openMenu (member:Member){
    //        val player = member.onlinePlayer ?:return
    //                val island = this.manager.getIsland(member) ?:return
    //
    //                val gui = this.createGUI(player)
    //        this.put(gui, "border-item", player)
    //        this.put(gui, "go-back", player) {
    //            this.rosePlugin.getManager < skyblock.manager.MenuManager > ()[dev.oribuin.skyblock.gui.PanelGUI:: class].openMenu(member)
    //        }
    //        this.setItems(gui, member, island)
    //        this.addExtraItems(gui, player)
    //
    //        gui.open(player)
    //    }
    //
    //    /**
    //     * Set all the dynamic items that need to be updated.
    //     *
    //     * @param gui The GUI to set the items to.
    //     * @param member The member to get the data from.
    //     */
    //    private fun setItems (gui:Gui, member:Member, island:dev.oribuin.skyblock.island.Island){
    //        val player = member.onlinePlayer ?:return
    //
    //                this.put(
    //                        gui,
    //                        "island-name",
    //                        player,
    //                        StringPlaceholders.of("island_name", island.settings.name)
    //                ) {
    //            this.setIslandName(gui, member, island)
    //        }
    //
    //        this.put(
    //                gui,
    //                "island-public",
    //                player,
    //                this.getBooleanPlc(island.settings. public,"#77dd77<bold>Public", "#dd7777<bold>Private")
    //        ){
    //            this.setOption(gui, member, island) {
    //                island ->
    //                        island.settings. public =!island.settings. public
    //                this.manager.sendMembersMessage(
    //                        island,
    //                        "command-settings-changed",
    //                        this.getSettingPlc("Island Privacy", if (island.settings. public)"Public" else"Private", player)
    //                )
    //            }
    //        }
    //
    ////        }
    //
    //        this.put(
    //                gui,
    //                "island-animals",
    //                player,
    //                this.getBooleanPlc(island.settings.animalSpawning, "#77dd77<bold>Enabled", "#dd7777<bold>Disabled")
    //        ) {
    //            this.setOption(gui, member, island) {
    //                island ->
    //                        island.settings.animalSpawning = !island.settings.animalSpawning
    //                this.manager.sendMembersMessage(
    //                        island,
    //                        "command-settings-changed",
    //                        this.getSettingPlc("Animal Spawning", if (island.settings.animalSpawning) "Enabled"
    //                else "Disabled", player)
    //                )
    //
    //            }
    //        }
    //
    //        this.put(
    //                gui,
    //                "island-mobs",
    //                player,
    //                this.getBooleanPlc(island.settings.mobSpawning, "#77dd77<bold>Enabled", "#dd7777<bold>Disabled")
    //        ) {
    //            this.setOption(gui, member, island) {
    //                island ->
    //                        island.settings.mobSpawning = !island.settings.mobSpawning
    //                this.manager.sendMembersMessage(
    //                        island,
    //                        "command-settings-changed",
    //                        this.getSettingPlc("Mob Spawning", if (island.settings.mobSpawning) "Enabled"
    //                else "Disabled", player)
    //                )
    //            }
    //        }
    //
    //        gui.update()
    //    }
    //
    //    /**
    //     * Create an anvil gui to change the island name.
    //     *
    //     * @param gui The GUI to set the items to.
    //     * @param member The member to get the data from.
    //     */
    //    private fun setIslandName (gui:Gui, member:Member, island:dev.oribuin.skyblock.island.Island){
    //
    //        val player = member.onlinePlayer ?:return
    //        if (member.role == Member.Role.MEMBER) {
    //            this.rosePlugin.send(player, "island-no-permission")
    //            gui.close(player)
    //        }
    //
    //        AnvilGUI.Builder()
    //                .plugin(this.rosePlugin)
    //                .title(HexUtils.colorify(island.settings.name))
    //                .itemLeft(skyblock.util.ItemBuilder.filler(Material.NAME_TAG))
    //                .onClick {
    //            slot, snapshot ->
    //            if (slot != AnvilGUI.Slot.OUTPUT) {
    //                return @onClick listOf(AnvilGUI.ResponseAction.close())
    //            }
    //
    //            if (snapshot.text.equals(island.settings.name, ignoreCase = true))
    //                return @onClick listOf(AnvilGUI.ResponseAction.close())
    //
    //            island.settings.name = snapshot.text
    //            this.cooldown[snapshot.player.uniqueId] = System.currentTimeMillis()
    //            island.cache(this.rosePlugin)
    //
    //            this.setItems(gui, member, island)
    //
    //            this.manager.sendMembersMessage(
    //                    island, "command-settings-changed", StringPlaceholders.of(
    //                            "setting", "Island Name",
    //                            "value", snapshot.text
    //                    )
    //            )
    //
    //            return @onClick listOf(AnvilGUI.ResponseAction.close())
    //        }
    //            .open(player)
    //    }
    //
    //    /**
    //     * Set an option for the island.
    //     *
    //     * @param gui The GUI to set the items to.
    //     * @param member The member to get the data from.
    //     * @param consumer The consumer to run when the option is changed.
    //     */
    //    private fun setOption (gui:Gui, member:Member, island:dev.oribuin.skyblock.island.Island, consumer:Consumer<dev.oribuin.skyblock.island.Island>){
    //
    //        val player = member.onlinePlayer ?:return
    //        if (member.role == Member.Role.MEMBER) {
    //            this.rosePlugin.send(player, "island-no-permission")
    //            gui.close(player)
    //        }
    //
    //        consumer.accept(island)
    //        island.cache(this.rosePlugin)
    //        this.setItems(gui, member, island)
    //    }
    //
    //    private fun getSettingPlc (name:String, value:String, player:Player) =StringPlaceholders.builder("setting", name)
    //            .add("value", value)
    //            .add("who", player.name)
    //            .build()
    //
    //    private fun getBooleanPlc (value:Boolean, enabled:String, disabled:String) =
    //    StringPlaceholders.builder("value", if (value) enabled
    //    else disabled)
    //            .build()
    //
    //    override val String
    //    get() = "settings"
    //
    //    override val Map<String, Any>
    //    get() = mapOf(
    //            "#0"to"GUI Settings",
    //            "gui-settings.title"to"Island Settings",
    //            "gui-settings.rows"to 3,
    //
    //            "#1"to"Border Item",
    //            "border-item.enabled"to true,
    //            "border-item.material" to Material.BLACK_STAINED_GLASS_PANE.toString(),
    //            "border-item.name" to " ",
    //            "border-item.slots" to listOf ("0-26"),
    //
    //            "#2" to "Island Name",
    //            "island-name.enabled" to true,
    //            "island-name.slot" to 13,
    //            "island-name.material" to Material.NAME_TAG.toString(),
    //            "island-name.name" to <#a6b2fc><bold>Island Name <gray>| <white>%island_name%",
    //            "island-name.lore" to listOf (
    //            " <white>| <gray>Click to change your",
    //            " <white>| <gray>current island name.",
    //            " <white>| ",
    //            " <white>| <gray>Requires #a6b2fcAdmin <gray>role!"
    //            ),
    //
    //    "#3" to "Island Privacy",
    //            "island-public.enabled" to true,
    //            "island-public.slot" to 14,
    //            "island-public.material" to Material.OAK_FENCE_GATE.toString(),
    //            "island-public.name" to <#a6b2fc><bold>Island Privacy <gray>| <white>%value%",
    //            "island-public.lore" to listOf (
    //            " <white>| <gray>Click to change your",
    //            " <white>| <gray>current island privacy.",
    //            " <white>| ",
    //            " <white>| <gray>Requires #a6b2fcAdmin <gray>role!"
    //            ),
    //
    //    "#4" to "Animal Spawning",
    //            "island-animals.enabled" to true,
    //            "island-animals.slot" to 15,
    //            "island-animals.material" to Material.WHEAT.toString(),
    //            "island-animals.name" to <#a6b2fc><bold>Animal Spawning <gray>| <white>%value%",
    //            "island-animals.lore" to listOf (
    //            " <white>| <gray>Click to toggle animal",
    //            " <white>| <gray>spawning on your island.",
    //            " <white>| ",
    //            " <white>| <gray>Requires #a6b2fcAdmin <gray>role!"
    //            ),
    //
    //    "#5" to "Mob Spawning",
    //            "island-mobs.enabled" to true,
    //            "island-mobs.slot" to 16,
    //            "island-mobs.material" to Material.BLAZE_ROD.toString(),
    //            "island-mobs.name" to <#a6b2fc><bold>Mob Spawning <gray>| <white>%value%",
    //            "island-mobs.lore" to listOf (
    //            " <white>| <gray>Click to toggle hostile mob",
    //            " <white>| <gray>spawning on your island.",
    //            " <white>| ",
    //            " <white>| <gray>Requires #a6b2fcAdmin <gray>role!"
    //            ),
    //
    //    "#6" to "Go Back",
    //            "go-back.enabled" to true,
    //            "go-back.slot" to 10,
    //            "go-back.material" to Material.PLAYER_HEAD.toString(),
    //            "go-back.name" to <#a6b2fc><bold>Go Back",
    //            "go-back.lore" to listOf (
    //            " <white>| <gray>Click to go back to",
    //            " <white>| <gray>the main page."
    //            ),
    //    "go-back.texture" to "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ=="
    //
    //        )
}
