package xyz.oribuin.skyblock.gui

import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Member

class WarpSettingsGUI(private val plugin: SkyblockPlugin, private val island: Island) {

    fun create(member: Member) {
        val player = member.offlinePlayer.player ?: return

    }

}