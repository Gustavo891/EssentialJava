package gustavo.essentialwarehouse.SpecialTool;

import gustavo.essentialwarehouse.EssentialWarehouse;
import gustavo.essentialwarehouse.SpecialTool.enchantments.AreaBreakEnchant;
import gustavo.essentialwarehouse.SpecialTool.enchantments.EnchantmentList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ToolItem {

    private final NamespacedKey AXE_LEVEL_KEY = new NamespacedKey("essential_warehouse", "axe_level");
    private final NamespacedKey AXE_XP_KEY = new NamespacedKey("essential_warehouse", "axe_xp");
    private final NamespacedKey AXE_BLOCKS_BREAK = new NamespacedKey("essential_warehouse", "blocks_break");
    private final NamespacedKey AXE_ENCHANTMENT_AREABREAK = new NamespacedKey("essential_warehouse", "enchantment_areabreak");
    private final NamespacedKey AXE_ENCHANTMENT_EXPERIENTE = new NamespacedKey("essential_warehouse", "enchantment_experiente");

    private final List<Material> blocks = new ArrayList<>(
            Arrays.asList(
                    Material.MELON,
                    Material.CARROT,
                    Material.NETHER_WART,
                    Material.POTATO,
                    Material.WHEAT,
                    Material.PUMPKIN
            )
    );

    protected final int BASE_XP = 1000;
    protected final double XP_MULTIPLIER = 1.5;

    public ToolItem(EssentialWarehouse essentialWarehouse) {
        essentialWarehouse.getServer().getPluginManager().registerEvents(new AreaBreakEnchant(this), essentialWarehouse);
    }

    public ItemStack createSpecialHoe() {
        ItemStack hoe = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = hoe.getItemMeta();
        assert meta != null;
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.DIG_SPEED, 100, true);

        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(AXE_LEVEL_KEY, PersistentDataType.INTEGER, 1);
        data.set(AXE_XP_KEY, PersistentDataType.INTEGER, 0);
        data.set(AXE_BLOCKS_BREAK, PersistentDataType.INTEGER, 0);
        data.set(AXE_ENCHANTMENT_AREABREAK, PersistentDataType.INTEGER, 0);
        updateLore(meta);
        hoe.setItemMeta(meta);
        return hoe;
    }

    public List<Material> getBlocks() {
        return blocks;
    }

    public boolean isSpecialHoe(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(AXE_LEVEL_KEY, PersistentDataType.INTEGER);
    }

    public void addExperience(ItemStack item, Material material, Player player) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        PersistentDataContainer data = meta.getPersistentDataContainer();

        int currentXp = data.getOrDefault(AXE_XP_KEY, PersistentDataType.INTEGER, 0);
        int currentLevel = data.getOrDefault(AXE_LEVEL_KEY, PersistentDataType.INTEGER, 1);
        int blocksBreaked = data.getOrDefault(AXE_BLOCKS_BREAK, PersistentDataType.INTEGER, 0);
        int experiente = data.getOrDefault(AXE_ENCHANTMENT_EXPERIENTE, PersistentDataType.INTEGER, 0);
        int xpAdd = 0;

        switch (material) {
            case MELON, PUMPKIN:
                xpAdd += 2;
                break; // Adicionado break
            case WHEAT, POTATO, CARROT, NETHER_WART:
                xpAdd += 5;
                break; // Adicionado break
        }
        if(experiente > 0) {
            float multiplier = ((float) experiente/50) * 10;
            currentXp += (int) (xpAdd * multiplier);
        } else {
            currentXp += xpAdd;
        }

        int xpToNextLevel = getXpToNextLevel(currentLevel);
        while (currentXp >= xpToNextLevel) {
            currentXp -= xpToNextLevel;
            currentLevel++;
            player.sendMessage(ChatColor.GREEN + "§ASua enxada foi nivelada para o nível §f" + currentLevel + "§a!");
            xpToNextLevel = getXpToNextLevel(currentLevel);
        }
        data.set(AXE_BLOCKS_BREAK, PersistentDataType.INTEGER, blocksBreaked + 1);
        data.set(AXE_XP_KEY, PersistentDataType.INTEGER, currentXp);
        data.set(AXE_LEVEL_KEY, PersistentDataType.INTEGER, currentLevel);
        updateLore(meta);
        item.setItemMeta(meta);
    }

    private int getXpToNextLevel(int level) {
        if (level == 1) {
            return BASE_XP;
        } else {
            return (int) (getXpToNextLevel(level - 1) * XP_MULTIPLIER);
        }
    }

    private void updateLore(ItemMeta meta) {
        int currentLevel = meta.getPersistentDataContainer().getOrDefault(AXE_LEVEL_KEY, PersistentDataType.INTEGER, 0);
        int levelArea = meta.getPersistentDataContainer().getOrDefault(AXE_ENCHANTMENT_AREABREAK, PersistentDataType.INTEGER,0);
        int experiente = meta.getPersistentDataContainer().getOrDefault(AXE_ENCHANTMENT_EXPERIENTE, PersistentDataType.INTEGER,0);
        List<String> lore = new ArrayList<>();
        lore.add("§7Efficiência ∞");

        if(levelArea > 0) {
            lore.add("§7Quebra-área " + levelArea);
        }
        if(meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS) > 0) {
            lore.add("§7Fortuna " + meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS));
        }
        if(experiente > 0) {
            lore.add("§7Experiente " + experiente);
        }
        lore.add("");

        int currentXp = meta.getPersistentDataContainer().getOrDefault(AXE_XP_KEY, PersistentDataType.INTEGER, 0);
        int blocksBreak = meta.getPersistentDataContainer().getOrDefault(AXE_BLOCKS_BREAK, PersistentDataType.INTEGER, 0);

        int xpToNextLevel = getXpToNextLevel(currentLevel);
        lore.add(ChatColor.GRAY + "  §fExperiência: §7" + currentXp + "/" + xpToNextLevel);
        lore.add("    " + barraProgresso(currentXp, xpToNextLevel) + "      ");
        lore.add("");

        meta.addEnchant(Enchantment.DIG_SPEED, 100, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        meta.setDisplayName(net.md_5.bungee.api.ChatColor.of(new Color(16, 128, 46)) + "Machado Especial §l" + currentLevel + " §7[" + blocksBreak + "]");
        meta.setLore(lore);
    }

    public String barraProgresso(int xp, int necessario) {

        int barrasTotais = 10;
        double percentual = ((double) xp /necessario)*100;
        percentual = Math.min(percentual, 100);

        int barrasPreenchidas = (int) (percentual / 100 * barrasTotais);
        int barrasVazias = barrasTotais - barrasPreenchidas;

        return "§2" + "⬛".repeat(Math.max(0, barrasPreenchidas)) +
                "§8" +
                "⬛".repeat(Math.max(0, barrasVazias)) +
                " §7" + (int) percentual + "%";
    }

    public int getEnchantment(EnchantmentList enchantment, ItemStack item) {

        return switch (enchantment) {
            case AREA_BREAK ->
                    Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().getOrDefault(AXE_ENCHANTMENT_AREABREAK, PersistentDataType.INTEGER, 0);
            case FORTUNE -> item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            case EXPERIENTE ->
                    Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().getOrDefault(AXE_ENCHANTMENT_EXPERIENTE, PersistentDataType.INTEGER, 0);
        };

    }

    public int getPrice(EnchantmentList enchantment, int level) {

        return switch (enchantment) {
            case AREA_BREAK -> {
                double multiplierArea = 2;
                yield (int) (3500 * ((level + 1) * multiplierArea));
            }
            case FORTUNE -> {
                double multiplierFortuna = 1.5;
                yield (int) (1500 * ((level + 1) * multiplierFortuna));
            }
            case EXPERIENTE -> {
                double multiplierExperiente = 2.5;
                yield (int) (4500 * ((level + 1) * multiplierExperiente));
            }
        };

    }

    public void setEnchantment(EnchantmentList enchantment, ItemStack item, int level, Player player) {
        switch (enchantment) {
            case AREA_BREAK:
                if(level > 0 && level <= 100) {
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    data.set(AXE_ENCHANTMENT_AREABREAK, PersistentDataType.INTEGER, level);
                    player.sendMessage("§aVocê melhorou a §lQuebra-Área§r§a de seu machado para §f" + level + "§a!");
                    updateLore(meta);
                    item.setItemMeta(meta);
                    break;
                } else {
                    return;
                }
            case EXPERIENTE:
                if(level > 0 && level <= 50) {
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    data.set(AXE_ENCHANTMENT_EXPERIENTE, PersistentDataType.INTEGER, level);
                    player.sendMessage("§aVocê melhorou a §lExperiente§r§a de seu machado para §f" + level + "§a!");
                    updateLore(meta);
                    item.setItemMeta(meta);
                    break;
                } else {
                    return;
                }
            case FORTUNE:
                if(level > 0 && level <= 20) {
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, level, true);
                    updateLore(meta);
                    player.sendMessage("§aVocê melhorou a §lFortuna§r§a de seu machado para §f" + level + "§a!");
                    item.setItemMeta(meta);
                    break;
                } else {
                    return;
                }
        }
    }
}
