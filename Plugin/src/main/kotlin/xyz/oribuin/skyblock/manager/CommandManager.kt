package xyz.oribuin.skyblock.manager

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.manager.AbstractCommandManager
import xyz.oribuin.skyblock.command.IslandCommandWrapper

class CommandManager(rosePlugin: RosePlugin) : AbstractCommandManager(rosePlugin) {

    override fun getRootCommands(): MutableList<Class<out RoseCommandWrapper>> {
        return mutableListOf(IslandCommandWrapper::class.java)
    }

    override fun getArgumentHandlerPackages(): MutableList<String> {
        return mutableListOf("xyz.oribuin.skyblock.command.argument")

    }
}