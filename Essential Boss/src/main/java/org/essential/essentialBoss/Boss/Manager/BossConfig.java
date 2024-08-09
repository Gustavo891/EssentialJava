package org.essential.essentialBoss.Boss.Manager;


import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.essential.essentialBoss.EssentialBoss;
import org.essential.essentialBoss.Boss.Reward.Reward;

import java.util.ArrayList;
import java.util.List;

public class BossConfig {

    private final EssentialBoss plugin;

    private final NamespacedKey BOSS_TYPE = new NamespacedKey("essential_boss", "boss_type");
    private final NamespacedKey BOSS_QUANTITY = new NamespacedKey("essential_boss", "boss_quantity");

    private final List<BossSettings> bossSettingsMap = new ArrayList<>();

    public BossConfig(EssentialBoss plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();

        plugin.getLogger().info("Carregando configurações do boss...");

        ConfigurationSection bossesSection = config.getConfigurationSection("bosses");
        if (bossesSection != null) {
            plugin.getLogger().info("Seção de bosses encontrada.");

            for (String key : bossesSection.getKeys(false)) {
                ConfigurationSection bossSection = bossesSection.getConfigurationSection(key);
                if (bossSection != null) {
                    String name = bossSection.getString("nome");
                    plugin.getLogger().info("Boss registrado: " + name);
                    int health = bossSection.getInt("vida");
                    String head = bossSection.getString("head");
                    EntityType entityType = EntityType.valueOf(bossSection.getString("entidade"));

                    List<Reward> rewards = new ArrayList<>();
                    ConfigurationSection rewardsSection = bossSection.getConfigurationSection("recompensas");
                    if (rewardsSection != null) {
                        plugin.getLogger().info("Seção de recompensas encontrada.");
                        for (String rewardKey : rewardsSection.getKeys(false)) {
                            ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(rewardKey);
                            if (rewardSection != null) {
                                String rewardName = rewardSection.getString("nome");
                                ConfigurationSection itemStackSection = rewardSection.getConfigurationSection("itemstack");
                                Material itemMaterial = Material.valueOf(itemStackSection.getString("type"));
                                int quantity = itemStackSection.getInt("quantidade");
                                List<String> commands = rewardSection.getStringList("comandos");
                                rewards.add(new Reward(rewardName, itemMaterial, quantity, commands));
                            }
                        }
                    }
                    bossSettingsMap.add(new BossSettings(name, health, entityType, rewards, head));
                }
            }
        } else {
            plugin.getLogger().warning("Seção de bosses não encontrada no config.yml.");
        }
    }

    public BossSettings getBossSettings(String bossType) {
        for(BossSettings bossSettings : bossSettingsMap) {
            if(bossSettings.getName().equalsIgnoreCase(bossType)) {
                return bossSettings;
            }
        }
        return null;
    }

    public String barraProgresso(int xp, int necessario) {

        int barrasPreenchidas;
        int barrasVazias;
        int barrasTotais = 10;
        double percentual = ((double) xp /necessario)*100;
        percentual = Math.min(percentual, 100);

        if(percentual <= 10) {
            barrasPreenchidas = 1;
            barrasVazias = 9;
        } else {
            barrasPreenchidas = (int) (percentual / 100 * barrasTotais);
            barrasVazias = barrasTotais - barrasPreenchidas;
        }

        return "§2" + "⬛".repeat(Math.max(0, barrasPreenchidas)) +
                "§8" +
                "⬛".repeat(Math.max(0, barrasVazias));
    }


}
