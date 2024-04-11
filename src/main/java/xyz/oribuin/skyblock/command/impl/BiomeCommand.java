package xyz.oribuin.skyblock.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import xyz.oribuin.skyblock.gui.BiomesGUI;
import xyz.oribuin.skyblock.util.asMember;
import xyz.oribuin.skyblock.util.asPlayer;
import xyz.oribuin.skyblock.util.getMenu;
import xyz.oribuin.skyblock.util.send;

class BiomeCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(context: CommandContext) {
        val player = context.asPlayer()
        val member = player.asMember(this.rosePlugin)

        if (!member.hasIsland) {
            this.rosePlugin.send(player, "no-island")
            return
        }

        this.rosePlugin.getMenu(xyz.oribuin.skyblock.gui.BiomesGUI::class).openMenu(member)
    }

    override fun getDefaultName(): String = "biome"

    override fun getDescriptionKey(): String = "command-biome-description"

    override fun getRequiredPermission(): String = "skyblock..biome"

    override fun isPlayerOnly(): Boolean = true


}