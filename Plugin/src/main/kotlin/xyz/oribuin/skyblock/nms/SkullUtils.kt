package xyz.oribuin.minions.util.nms

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import dev.rosewood.rosegarden.utils.NMSUtil
import me.arcaniax.hdb.api.HeadDatabaseAPI
import org.bukkit.Bukkit
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Method
import java.net.URL
import java.util.*

object SkullUtils {

    private var method_SkullMeta_setProfile: Method? = null

    /**
     * Applies a base64 encoded texture to an item's SkullMeta
     *
     * @param skullMeta The ItemMeta for the Skull
     * @param texture   The texture to apply to the skull
     */
    @Suppress("deprecation")
    fun setSkullTexture(skullMeta: SkullMeta, texture: String?) {
        var newTexture = texture

        if (newTexture != null && newTexture.startsWith("hdb:") && Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
            newTexture = HeadDatabaseAPI().getBase64(newTexture.substring(4))
        }

        if (newTexture.isNullOrEmpty()) return

        if (NMSUtil.getVersionNumber() >= 18) { // No need to use NMS on 1.18.1+
            if (NMSUtil.isPaper()) {
                setTexturesPaper(skullMeta, newTexture)
                return
            }

            val profile = Bukkit.createPlayerProfile(UUID.nameUUIDFromBytes(newTexture.toByteArray()), "")
            val textures = profile.textures
            val decodedTextureJson = String(Base64.getDecoder().decode(newTexture))
            val decodedTextureUrl = decodedTextureJson.substring(28, decodedTextureJson.length - 4)

            textures.skin = URL(decodedTextureUrl)
            profile.setTextures(textures)
            skullMeta.ownerProfile = profile
            return
        }

        // 1.17 and below
        val profile = GameProfile(UUID.nameUUIDFromBytes(newTexture.toByteArray()), "")
        profile.properties.put("textures", Property("textures", newTexture))
        try {
            if (method_SkullMeta_setProfile == null) {
                method_SkullMeta_setProfile = skullMeta.javaClass.getDeclaredMethod("setProfile", GameProfile::class.java)
                method_SkullMeta_setProfile?.isAccessible = true
            }
            method_SkullMeta_setProfile?.invoke(skullMeta, profile)
        } catch (e: ReflectiveOperationException) {
            Bukkit.getLogger().severe("Failed to set skull texture: " + e.message)
        }
    }

    /**
     * Set the texture using the paper api for 1.18+
     *
     * @param meta    The skull meta
     * @param texture The texture
     */
    private fun setTexturesPaper(meta: SkullMeta, texture: String?) {
        if (texture.isNullOrEmpty()) return

        val profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(texture.toByteArray()), "")
        val textures = profile.textures
        val decodedTextureJson = String(Base64.getDecoder().decode(texture))
        val decodedTextureUrl = decodedTextureJson.substring(28, decodedTextureJson.length - 4)
        textures.skin = URL(decodedTextureUrl)

        profile.setTextures(textures)
        meta.playerProfile = profile
    }

}