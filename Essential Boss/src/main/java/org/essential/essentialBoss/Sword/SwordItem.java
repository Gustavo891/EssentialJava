package org.essential.essentialBoss.Sword;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SwordItem {

    private final NamespacedKey MATADORA = new NamespacedKey("essential_boss", "matadora");
    private final NamespacedKey MATADORA_DANO = new NamespacedKey("essential_boss", "matadora_dano");
    private final NamespacedKey STATS_MATADOS = new NamespacedKey("essential_boss", "matadora_bosses_matados");

    public ItemStack giveSword() {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        assert swordMeta != null;
        swordMeta.setUnbreakable(true);

        swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        swordMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        swordMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        PersistentDataContainer swordData = swordMeta.getPersistentDataContainer();
        swordData.set(MATADORA, PersistentDataType.BOOLEAN, true);
        swordData.set(MATADORA_DANO, PersistentDataType.INTEGER, 1);
        swordData.set(STATS_MATADOS, PersistentDataType.INTEGER, 0);
        updateLore(swordMeta);
        sword.setItemMeta(swordMeta); // Atualize o ItemMeta após alterar os dados persistentes

        return sword;
    }

    public void updateStatsKills(ItemStack sword) {
        PersistentDataContainer data = sword.getItemMeta().getPersistentDataContainer();
        int matados = data.get(STATS_MATADOS, PersistentDataType.INTEGER);
        data.set(STATS_MATADOS, PersistentDataType.INTEGER, matados + 1);
    }

    public void updateLore(ItemMeta meta) {
        PersistentDataContainer swordData = meta.getPersistentDataContainer();
        int dano = 1;
        int matados = 0;
        try {
            dano = swordData.get(MATADORA_DANO, PersistentDataType.INTEGER);
            matados = swordData.get(STATS_MATADOS, PersistentDataType.INTEGER);
        } catch (NullPointerException ignored) {
        }
        Color cor = new Color(153, 47, 47);

        meta.setDisplayName(ChatColor.of(cor) + "⚔ Matadora §7[" + matados + "]");
        List<String> lore = new ArrayList<>();
        lore.add("§7Dano " + dano);
        meta.setLore(lore);
    }

    public boolean isSword(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false; // Verifica se o item ou o ItemMeta são nulos
        }

        PersistentDataContainer swordData = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
        return swordData.has(MATADORA, PersistentDataType.BOOLEAN);
    }

    public int getDano(ItemStack item) {
        PersistentDataContainer swordData = item.getItemMeta().getPersistentDataContainer();
        if(swordData.has(MATADORA_DANO, PersistentDataType.INTEGER)) {
            return swordData.get(MATADORA_DANO, PersistentDataType.INTEGER);
        } else {
            return -1;
        }
    }

    public int getPrice(String enchant, int level) {
        switch (enchant.toLowerCase()) {
            case "dano":
                int inicial = 1000;
                double multiplier = 3.5;
                return (int) (inicial * ((level + 1) * multiplier));
        }
        return -1;
    }

    public void setEnchantment(String enchant, int level, ItemStack item) {
        ItemMeta swordMeta = item.getItemMeta();
        switch (enchant.toLowerCase()) {
            case "dano":
                PersistentDataContainer swordData = swordMeta.getPersistentDataContainer();
                swordData.set(MATADORA_DANO, PersistentDataType.INTEGER, level + 1);
                updateLore(swordMeta);
                item.setItemMeta(swordMeta);
        }
    }


    public static String formatNumber(double number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000);
        }
        else if (number >= 1_000) {
            return String.format("%.1fk", number / 1_000);
        }
        else {
            return String.valueOf((int) number);
        }
    }
}
