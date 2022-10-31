package xyz.oribuin.skyblock.command

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper

class IslandCommandWrapper(rosePlugin: RosePlugin?) : RoseCommandWrapper(rosePlugin) {

    override fun getDefaultName(): String = "island"

    override fun getDefaultAliases(): MutableList<String>  = mutableListOf("is", "sb", "skyblock")

    override fun getCommandPackages(): MutableList<String> = mutableListOf("xyz.oribuin.skyblock.command.command")

    override fun includeBaseCommand(): Boolean = false

    override fun includeHelpCommand(): Boolean = true

    override fun includeReloadCommand(): Boolean = true

}