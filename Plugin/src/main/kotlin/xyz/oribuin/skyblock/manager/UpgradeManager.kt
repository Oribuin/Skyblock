package xyz.oribuin.skyblock.manager

import org.apache.commons.lang.WordUtils
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import xyz.oribuin.orilibrary.manager.Manager
import xyz.oribuin.orilibrary.util.FileUtils
import xyz.oribuin.skyblock.SkyblockPlugin
import xyz.oribuin.skyblock.island.Island
import xyz.oribuin.skyblock.island.Upgrade
import xyz.oribuin.skyblock.upgrade.IslandUpgrade
import xyz.oribuin.skyblock.util.parseEnum
import kotlin.math.max

class UpgradeManager(private val plugin: SkyblockPlugin) : Manager(plugin) {

    private lateinit var config: FileConfiguration
    private val islandUpgradeMap = mutableMapOf<IslandUpgrade.Type, IslandUpgrade>()

    override fun enable() {
        val file = FileUtils.createFile(plugin, "upgrades.yml")
        this.config = YamlConfiguration.loadConfiguration(file)
        this.loadUpgrades()
    }

    /**
     * Get an island's size from their upgrade tier.
     *
     * @param island The island where we get the tier.
     * @return The island size
     */
    fun getIslandSize(island: Island): Int {
        val tiers = this.islandUpgradeMap[IslandUpgrade.Type.SIZE] ?: return 150
        return (tiers.tiers.entries.find { entry -> entry.key == island.upgrade.sizeTier } ?: return 150).value.value as Int
    }

    /**
     * Get the maximum allowed chest generators for an island
     *
     * @param island The island that owns the upgrade
     * @return The max chest gen count
     */
    fun getMaxChestGens(island: Island): Int {
        val tiers = islandUpgradeMap[IslandUpgrade.Type.CHESTGEN] ?: return 1
        return (tiers.tiers.entries.find { entry -> entry.key == island.upgrade.chestGenTier } ?: return 1).value.value as Int
    }

    /**
     * Load all the upgrades and their tiers from the config file.
     */
    private fun loadUpgrades() {
        this.config.getKeys(false).forEach {
            val islandUpgradeType = parseEnum(IslandUpgrade.Type::class, it.uppercase())

            // Generate and get the default values for the GUI icon
            val icon = parseEnum(Material::class, this.config.getString("$it.icon") ?: "BARRIER")
            val name = this.config.getString("$it.name") ?: WordUtils.capitalize(islandUpgradeType.name.lowercase())
            val lore = this.config.getStringList("$it.lore")

            val islandUpgrade = IslandUpgrade(islandUpgradeType)
            islandUpgrade.icon = icon
            islandUpgrade.displayName = name
            islandUpgrade.lore = lore

            val tiersSection = this.config.getConfigurationSection("$it.tiers") ?: return
            for (tier in tiersSection.getKeys(false)) {
                val cost = max(tiersSection.getDouble("$tier.cost"), 1.0)
                val value = tiersSection.get("$tier.value") ?: 1
                islandUpgrade.tiers[tier.toIntOrNull() ?: return] = IslandUpgrade.Tier(cost, value)
            }

            this.islandUpgradeMap[islandUpgradeType] = islandUpgrade
        }
    }
}