package xyz.oribuin.skyblock.command.argument

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.ArgumentParser
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo
import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.Bukkit
import xyz.oribuin.skyblock.island.Member
import xyz.oribuin.skyblock.manager.IslandManager
import xyz.oribuin.skyblock.util.getManager

class MemberArgument(rosePlugin: RosePlugin) : RoseCommandArgumentHandler<Member>(rosePlugin, Member::class.java) {

    private val islandManager = this.rosePlugin.getManager<IslandManager>()

    override fun handleInternal(argumentInfo: RoseCommandArgumentInfo, argumentParser: ArgumentParser): Member {
        val input = argumentParser.next()

        val exception = HandledArgumentException("argument-handler-member-option", StringPlaceholders.single("input", input))

        return this.islandManager.getMember(Bukkit.getOfflinePlayerIfCached(input) ?: throw exception)
    }

    override fun suggestInternal(argumentInfo: RoseCommandArgumentInfo, argumentParser: ArgumentParser): MutableList<String> {
        argumentParser.next()
        return Bukkit.getOfflinePlayers().mapNotNull { it.name }.toMutableList()
    }

}