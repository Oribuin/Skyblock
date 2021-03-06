package xyz.oribuin.skyblock.manager

import xyz.oribuin.skyblock.Skyblock
import xyz.oribuin.skyblock.hook.PlaceholderExp
import xyz.oribuin.skyblock.hook.VaultHook
import xyz.oribuin.skyblock.library.Manager

class HookManager(plugin: Skyblock) : Manager(plugin) {
    override fun reload() {
        this.registerPAPI()
        this.registerVault()
    }


    private fun registerVault() {
        if (plugin.server.pluginManager.getPlugin("Vault") != null) {
            val vaultHook = VaultHook(plugin as Skyblock)
            vaultHook.setupEconomy()
            vaultHook.setupPermissions()
        }
    }

    private fun registerPAPI() {
        if (plugin.server.pluginManager.getPlugin("PlaceholderAPI") != null) {
            PlaceholderExp(plugin as Skyblock).register()
        }
    }

    override fun disable() {
        // Unused
    }

}
