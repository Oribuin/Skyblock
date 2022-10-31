package xyz.oribuin.skyblock.manager

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.locale.Locale
import dev.rosewood.rosegarden.manager.AbstractLocaleManager
import xyz.oribuin.skyblock.locale.EnglishLocale

class LocaleManager(rosePlugin: RosePlugin?) : AbstractLocaleManager(rosePlugin) {

    override fun getLocales(): MutableList<Locale> {
        return mutableListOf(EnglishLocale())
    }
}