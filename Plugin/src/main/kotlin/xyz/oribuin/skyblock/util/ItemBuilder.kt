package xyz.oribuin.skyblock.util

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import java.net.URL
import java.util.*

@Suppress("deprecation")
class ItemBuilder(private var item: ItemStack) {

    constructor(material: Material) : this(ItemStack(material))

    /**
     * Sets the name of the item
     *
     * @param name The name of the item
     * @return The item builder
     */
    fun name(name: String): ItemBuilder {
        val meta = this.item.itemMeta ?: return this
        meta.setDisplayName(name)
        item.itemMeta = meta;
        return this
    }

    /**
     * Sets the lore of the item
     *
     * @param lore The lore of the item
     * @return The item builder
     */
    fun lore(vararg lore: String): ItemBuilder {
        val meta = this.item.itemMeta ?: return this
        meta.lore = lore.toList()
        item.itemMeta = meta;
        return this
    }

    /**
     * Sets the lore of the item
     *
     * @param lore The lore of the item
     * @return The item builder
     */
    fun lore(lore: List<String>): ItemBuilder {
        val meta = this.item.itemMeta ?: return this
        meta.lore = lore.toList()
        item.itemMeta = meta;
        return this
    }

    /**
     * Sets the amount of the item
     *
     * @param amount The amount of the item
     * @return The item builder
     */
    fun amount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
    }

    /**
     * Adds an enchantment to the item
     *
     * @param enchantment The enchantment to add
     * @param level The level of the enchantment
     * @return The item builder
     */
    fun enchant(enchantment: Enchantment, level: Int): ItemBuilder {
        val meta = this.item.itemMeta ?: return this
        meta.addEnchant(enchantment, level, true)
        item.itemMeta = meta
        return this
    }

    fun flags(flags: Array<ItemFlag>): ItemBuilder {
        val meta = this.item.itemMeta ?: return this
        meta.addItemFlags(*flags)
        item.itemMeta = meta
        return this
    }

    /**
     * Sets the item to be unbreakable
     *
     * @return The item builder
     */
    fun unbreakable(): ItemBuilder {
        val meta = this.item.itemMeta ?: return this
        meta.isUnbreakable = true
        item.itemMeta = meta
        return this
    }

    /**
     * Sets the item to glow
     *
     * @return The item builder
     */
    fun glow(enabled: Boolean): ItemBuilder {

        if (!enabled)
            return this

        val meta = this.item.itemMeta ?: return this
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.itemMeta = meta
        return this
    }

    /**
     * Sets the item skull texture
     *
     * @param texture The texture of the skull
     * @return The item builder
     */
    fun texture(texture: String): ItemBuilder {
        val meta = this.item.itemMeta ?: return this
        if (meta !is SkullMeta)
            return this

        if (texture.isEmpty())
            return this

        val profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(texture.toByteArray()), "")
        val textures = profile.textures
        val decodedTextureJson = String(Base64.getDecoder().decode(texture))
        val decodedTextureUrl = decodedTextureJson.substring(28, decodedTextureJson.length - 4)
        textures.skin = URL(decodedTextureUrl)

        item.itemMeta = meta
        return this
    }

    /**
     * Sets the skull owner
     *
     * @param owner The owner of the skull
     * @return The item builder
     */
    fun owner(player: OfflinePlayer): ItemBuilder {
        val meta = this.item.itemMeta ?: return this
        if (meta !is SkullMeta)
            return this

        meta.owningPlayer = player
        item.itemMeta = meta
        return this
    }

    /**
     * Sets the item custom model data
     *
     * @param modelData The custom model data
     * @return The item builder
     */
    fun model(modelData: Int): ItemBuilder {
        val meta = this.item.itemMeta ?: return this
        meta.setCustomModelData(modelData)
        item.itemMeta = meta
        return this
    }

    /**
     * Add a potion effect to the item
     *
     * @param effect The potion effect
     * @return The item builder
     */
    fun potion(effect: PotionEffect): ItemBuilder {
        val meta = this.item.itemMeta ?: return this
        if (meta !is PotionMeta)
            return this

        meta.addCustomEffect(effect, true)
        item.itemMeta = meta
        return this
    }

    /**
     * Sets the potion color
     *
     * @param color The potion color
     * @return The item builder
     */
    fun potionColor(color: Color): ItemBuilder {

        val meta = this.item.itemMeta ?: return this
        if (meta !is PotionMeta)
            return this

        meta.color = color
        item.itemMeta = meta
        return this
    }

    /**
     * Sets the leather armor color
     *
     * @param color The leather armor color
     * @return The item builder
     */
    fun leatherColor(color: Color): ItemBuilder {
        val meta = this.item.itemMeta ?: return this
        if (meta !is LeatherArmorMeta)
            return this

        meta.setColor(color)
        item.itemMeta = meta
        return this
    }

    /**
     * Builds the item
     *
     * @return The item
     */
    fun build(): ItemStack = item

    companion object {
        fun filler(material: Material) = ItemBuilder(material)
            .amount(1)
            .name(" ")
            .build()

        fun guiFiller(material: Material): GuiItem = GuiItem(this.filler(material))
    }

}