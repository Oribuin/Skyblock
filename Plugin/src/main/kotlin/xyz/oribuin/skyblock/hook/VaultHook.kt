package xyz.oribuin.skyblock.hook

import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import xyz.oribuin.skyblock.SkyblockPlugin

class VaultHook {

    companion object {
        private lateinit var economy: Economy
        private lateinit var permission: Permission
        private lateinit var chat: Chat

        /**
         * Initialize the hook
         */
        private fun register() {
            if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                SkyblockPlugin.instance.logger.info("Hooking into Vault...")

                // Register plugin providers.
                economy = Bukkit.getServicesManager().getRegistration(Economy::class.java)?.provider!!
                permission = Bukkit.getServicesManager().getRegistration(Permission::class.java)?.provider!!
                chat = Bukkit.getServicesManager().getRegistration(Chat::class.java)?.provider!!
            }
        }

        /**
         * Get the primary group for the player.
         *
         * @param player The player to get the group for.
         * @return The group name.
         */
        fun getRank(player: Player): String = this.permission.getPrimaryGroup(player)

        /**
         * Get the primary group capitalized for the player.
         *
         * @param player The player to get the group for.
         * @return The group name.
         */
        fun getRankCapital(worldName: String, player: OfflinePlayer): String = this.permission.getPrimaryGroup(worldName, player)
            .replaceFirstChar { it.uppercase() }

        /**
         * Get the player's active prefix
         *
         * @param player The player to get the prefix for.
         * @return The prefix.
         */
        fun getPrefix(player: Player): String = this.chat.getPlayerPrefix(player)

        /**
         * Get the player's active balance
         *
         * @param player The player to get the balance for.
         * @return The balance.
         */
        fun getBalance(player: OfflinePlayer): Double = this.economy.getBalance(player)

        /**
         * Check if a player can afford a certain amount.
         *
         * @param player The player to check.
         * @param amount The amount to check.
         * @return True if the player can afford the amount.
         */
        fun has(player: OfflinePlayer, amount: Double): Boolean = this.economy.has(player, amount)

        /**
         * Withdraw money from a player.
         *
         * @param player The player to withdraw from.
         * @param amount The amount to withdraw.
         */
        fun withdraw(player: OfflinePlayer, amount: Double): Boolean = this.economy.withdrawPlayer(player, amount).transactionSuccess()

        /**
         * Deposit money to a player.
         *
         * @param player The player to deposit to.
         * @param amount The amount to deposit.
         */
        fun deposit(player: OfflinePlayer, amount: Double): Boolean = this.economy.depositPlayer(player, amount).transactionSuccess()
    }

    init {
        register()
    }

}