package xyz.oribuin.skyblock.nms

import org.bukkit.Bukkit

object NMSAdapter {

    lateinit var handler: NMSHandler
    var validVersion = true

    init {

        val name = Bukkit.getServer().javaClass.getPackage().name
        val version = name.substring(name.lastIndexOf('.') + 1)

        try {

            this.handler = Class.forName("xyz.oribuin.skyblock.nms.$version.NMSHandlerImpl").getConstructor().newInstance() as NMSHandler
        } catch (ignored: Exception) {
            Bukkit.getLogger().severe("We could not find support for version $version. Please contact the developer.")
            this.validVersion = false
        }
    }
}