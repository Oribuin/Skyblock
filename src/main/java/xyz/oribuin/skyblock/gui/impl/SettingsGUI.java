package xyz.oribuin.skyblock.gui.impl;

public class SettingsGUI {
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
    //            this.rosePlugin.getManager < skyblock.manager.MenuManager > ()[xyz.oribuin.skyblock.gui.PanelGUI:: class].openMenu(member)
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
    //    private fun setItems (gui:Gui, member:Member, island:xyz.oribuin.skyblock.island.Island){
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
    //                this.getBooleanPlc(island.settings. public,"#77dd77&lPublic", "#dd7777&lPrivate")
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
    //                this.getBooleanPlc(island.settings.animalSpawning, "#77dd77&lEnabled", "#dd7777&lDisabled")
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
    //                this.getBooleanPlc(island.settings.mobSpawning, "#77dd77&lEnabled", "#dd7777&lDisabled")
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
    //    private fun setIslandName (gui:Gui, member:Member, island:xyz.oribuin.skyblock.island.Island){
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
    //    private fun setOption (gui:Gui, member:Member, island:xyz.oribuin.skyblock.island.Island, consumer:Consumer<xyz.oribuin.skyblock.island.Island>){
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
    //            "island-name.name" to "#a6b2fc&lIsland Name &7| &f%island_name%",
    //            "island-name.lore" to listOf (
    //            " &f| &7Click to change your",
    //            " &f| &7current island name.",
    //            " &f| ",
    //            " &f| &7Requires #a6b2fcAdmin &7role!"
    //            ),
    //
    //    "#3" to "Island Privacy",
    //            "island-public.enabled" to true,
    //            "island-public.slot" to 14,
    //            "island-public.material" to Material.OAK_FENCE_GATE.toString(),
    //            "island-public.name" to "#a6b2fc&lIsland Privacy &7| &f%value%",
    //            "island-public.lore" to listOf (
    //            " &f| &7Click to change your",
    //            " &f| &7current island privacy.",
    //            " &f| ",
    //            " &f| &7Requires #a6b2fcAdmin &7role!"
    //            ),
    //
    //    "#4" to "Animal Spawning",
    //            "island-animals.enabled" to true,
    //            "island-animals.slot" to 15,
    //            "island-animals.material" to Material.WHEAT.toString(),
    //            "island-animals.name" to "#a6b2fc&lAnimal Spawning &7| &f%value%",
    //            "island-animals.lore" to listOf (
    //            " &f| &7Click to toggle animal",
    //            " &f| &7spawning on your island.",
    //            " &f| ",
    //            " &f| &7Requires #a6b2fcAdmin &7role!"
    //            ),
    //
    //    "#5" to "Mob Spawning",
    //            "island-mobs.enabled" to true,
    //            "island-mobs.slot" to 16,
    //            "island-mobs.material" to Material.BLAZE_ROD.toString(),
    //            "island-mobs.name" to "#a6b2fc&lMob Spawning &7| &f%value%",
    //            "island-mobs.lore" to listOf (
    //            " &f| &7Click to toggle hostile mob",
    //            " &f| &7spawning on your island.",
    //            " &f| ",
    //            " &f| &7Requires #a6b2fcAdmin &7role!"
    //            ),
    //
    //    "#6" to "Go Back",
    //            "go-back.enabled" to true,
    //            "go-back.slot" to 10,
    //            "go-back.material" to Material.PLAYER_HEAD.toString(),
    //            "go-back.name" to "#a6b2fc&lGo Back",
    //            "go-back.lore" to listOf (
    //            " &f| &7Click to go back to",
    //            " &f| &7the main page."
    //            ),
    //    "go-back.texture" to "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ=="
    //
    //        )
}
