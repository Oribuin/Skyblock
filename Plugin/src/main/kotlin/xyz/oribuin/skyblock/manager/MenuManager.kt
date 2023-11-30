package xyz.oribuin.skyblock.manager

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.manager.Manager
import xyz.oribuin.skyblock.gui.*
import kotlin.reflect.KClass

class MenuManager(rosePlugin: RosePlugin) : Manager(rosePlugin) {

    private var registeredMenus: MutableMap<KClass<out PluginGUI>, PluginGUI> = mutableMapOf()

    override fun reload() {
        this.registeredMenus = mutableMapOf(
            BiomesGUI::class to BiomesGUI(this.rosePlugin),
            BorderGUI::class to BorderGUI(this.rosePlugin),
            CreateGUI::class to CreateGUI(this.rosePlugin),
            MembersGUI::class to MembersGUI(this.rosePlugin),
            PanelGUI::class to PanelGUI(this.rosePlugin),
            SettingsGUI::class to SettingsGUI(this.rosePlugin),
            WarpCategoryGUI::class to WarpCategoryGUI(this.rosePlugin),
            WarpSettingsGUI::class to WarpSettingsGUI(this.rosePlugin),
            WarpsGUI::class to WarpsGUI(this.rosePlugin)
        )

        this.registeredMenus.forEach { it.value.load() }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : PluginGUI> get(menuClass: KClass<T>): T = this.registeredMenus[menuClass] as T

    override fun disable() = this.registeredMenus.clear()
}