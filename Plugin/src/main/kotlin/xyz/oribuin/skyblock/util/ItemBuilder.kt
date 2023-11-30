package xyz.oribuin.skyblock.util

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.oribuin.minions.util.nms.SkullUtils

@Suppress("unused")
class ItemBuilder {

    private var item: ItemStack

    constructor(material: Material) {
        item = ItemStack(material)
    }

    constructor(item: ItemStack) {
        this.item = item.clone()
    }

    fun setMaterial(material: Material?): ItemBuilder {
        item.type = material!!
        return this
    }

    /**
     * Set the ItemStack's Display Name.
     *
     * @param text The text.
     * @return Item.Builder.
     */
    @Suppress("deprecation")
    fun name(text: String?): ItemBuilder {
        val meta = item.itemMeta
        if (meta == null || text == null)
            return this

        meta.setDisplayName(text)
        item.setItemMeta(meta)
        return this
    }


    /**
     * Set the ItemStack's Lore
     *
     * @param lore The lore
     * @return Item.Builder.
     */
    @Suppress("deprecation")
    fun lore(lore: List<String>?): ItemBuilder {
        val meta = item.itemMeta
        if (meta == null || lore == null)
            return this

        meta.lore = lore
        item.setItemMeta(meta)
        return this
    }

    /**
     * Set the ItemStack's Lore
     *
     * @param lore The lore
     * @return Item.Builder.
     */
    fun lore(vararg lore: String): ItemBuilder {
        return this.lore(listOf(*lore))
    }

    /**
     * Set the ItemStack amount.
     *
     * @param amount The amount of items.
     * @return Item.Builder
     */
    fun amount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
    }

    /**
     * Add an enchantment to an item.
     *
     * @param ench  The enchantment.
     * @param level The level of the enchantment
     * @return Item.Builder
     */
    fun enchant(ench: Enchantment, level: Int): ItemBuilder {
        val meta = item.itemMeta ?: return this
        meta.addEnchant(ench, level, true)
        item.setItemMeta(meta)
        return this
    }

    /**
     * Add multiple enchantments to an item.
     *
     * @param enchantments The enchantments.
     * @return Item.Builder
     */
    fun enchant(enchantments: Map<Enchantment, Int>): ItemBuilder {
        val meta = item.itemMeta ?: return this
        for ((key, value) in enchantments) {
            meta.addEnchant(key, value, true)
        }

        item.setItemMeta(meta)
        return this
    }

    /**
     * Remove an enchantment from an Item
     *
     * @param ench The enchantment.
     * @return Item.Builder
     */
    fun remove(ench: Enchantment): ItemBuilder {
        item.removeEnchantment(ench)
        return this
    }

    /**
     * Remove and reset the ItemStack's Flags
     *
     * @param flags The ItemFlags.
     * @return Item.Builder
     */
    fun flags(flags: Array<ItemFlag>): ItemBuilder {
        val meta = item.itemMeta ?: return this
        meta.removeItemFlags(*ItemFlag.entries.toTypedArray())
        meta.addItemFlags(*flags)
        item.setItemMeta(meta)
        return this
    }

    /**
     * Change the item's unbreakable status.
     *
     * @param unbreakable true if unbreakable
     * @return Item.Builder
     */
    fun unbreakable(unbreakable: Boolean): ItemBuilder {
        val meta = item.itemMeta ?: return this
        meta.isUnbreakable = unbreakable
        item.setItemMeta(meta)
        return this
    }

    /**
     * Set an item to glow.
     *
     * @return Item.Builder
     */
    fun glow(b: Boolean): ItemBuilder {
        if (!b) return this
        val meta = item.itemMeta ?: return this
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.setItemMeta(meta)
        return this
    }

    fun texture(texture: String?): ItemBuilder {
        if (item.type != Material.PLAYER_HEAD || texture == null) return this
        val skullMeta = item.itemMeta as SkullMeta? ?: return this
        SkullUtils.setSkullTexture(skullMeta, texture)
        item.setItemMeta(skullMeta)
        return this
    }

    fun owner(owner: OfflinePlayer?): ItemBuilder {
        if (item.type != Material.PLAYER_HEAD || owner == null) return this
        val skullMeta = item.itemMeta as SkullMeta? ?: return this
        skullMeta.setOwningPlayer(owner)
        item.setItemMeta(skullMeta)
        return this
    }

    fun model(model: Int): ItemBuilder {
        val meta = item.itemMeta
        if (meta == null || model == -1) return this
        meta.setCustomModelData(model)
        item.setItemMeta(meta)
        return this
    }

    fun potion(effectType: PotionEffectType?, duration: Int, amp: Int): ItemBuilder {
        if (item.itemMeta !is PotionMeta) return this
        val meta = item.itemMeta as PotionMeta

        meta.addCustomEffect(PotionEffect(effectType!!, duration, amp), true)
        item.setItemMeta(meta)
        return this
    }

    fun color(color: Color?): ItemBuilder {
        if (color == null || item.itemMeta == null) return this

        val meta = item.itemMeta
        if (meta is PotionMeta) {
            meta.color = color
            item.setItemMeta(meta)
        }

        if (meta is LeatherArmorMeta) {
            meta.setColor(color)
            item.setItemMeta(meta)
        }

        return this
    }

    /**
     * Finalize the Item Builder and create the stack.
     *
     * @return The ItemStack
     */
    fun build(): ItemStack {
        return item
    }

    companion object {
        fun filler(material: Material) = ItemBuilder(material)
            .amount(1)
            .name(" ")
            .build()
    }

}