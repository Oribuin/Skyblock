package xyz.oribuin.skyblock.command.impl.sub.admin;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

class AdminKickCommand(rosePlugin:RosePlugin, parent:RoseCommandWrapper) :

RoseSubCommand(rosePlugin, parent) {

    override fun getDefaultName():String = "kick"

}