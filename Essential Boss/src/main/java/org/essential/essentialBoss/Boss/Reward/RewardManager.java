package org.essential.essentialBoss.Boss.Reward;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.essential.essentialBoss.Boss.Manager.BossSettings;

public class RewardManager {

    public static void giveRewards(Player player, int stackCount, BossSettings bossSettings) {
        for (Reward reward : bossSettings.getRewards()) {
            // Multiplicar a quantidade de itens pela quantidade de bosses derrotados
            int totalQuantity = reward.getQuantity() * stackCount;
            Material material = reward.getItemStack();
            if (material != null) {
                ItemStack itemStack = new ItemStack(material, totalQuantity);
                player.getInventory().addItem(itemStack);
            }
            // Executar comandos configurados
            for (String command : reward.getCommands()) {
                String formattedCommand = command.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand);
            }
        }
    }
}
