package gustavo.essentialwarehouse.SpecialTool.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import gustavo.essentialeconomy.Manager.CurrencyType;
import gustavo.essentialeconomy.Manager.EconomyManager;
import gustavo.essentialwarehouse.EssentialWarehouse;
import gustavo.essentialwarehouse.PlotManager.Base;
import gustavo.essentialwarehouse.SpecialTool.ToolItem;
import gustavo.essentialwarehouse.SpecialTool.enchantments.EnchantmentList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ToolGUI {

    ToolItem tool = EssentialWarehouse.getInstance().getToolItem();
    private final ItemStack machado;
    private final Player player;
    private final EconomyManager economyAPI = EssentialWarehouse.getInstance().getEconomyAPI();
    ChestGui menu = new ChestGui(4, "Machado Especial:");

    public ToolGUI(ItemStack item, Player player) {
        this.machado = item;
        this.player = player;
    }

    public void openMenu() {
        StaticPane painel = new StaticPane(0, 0,9,4);

        GuiItem fortune = setupFortune();
        GuiItem machadoGui = new GuiItem(machado);
        GuiItem area = setupArea();
        GuiItem experiente = setupExperiente();

        painel.addItem(fortune, 2, 2);
        painel.addItem(area, 4, 2);
        painel.addItem(experiente, 6, 2);

        painel.addItem(machadoGui, 4, 0);

        menu.addPane(painel);
        menu.show(player);

    }

    private void updateMenu() {
        StaticPane painel = new StaticPane(0, 0,9,4);
        GuiItem fortune = setupFortune();
        GuiItem machadoGui = new GuiItem(machado);
        GuiItem area = setupArea();
        GuiItem experiente = setupExperiente();
        painel.addItem(fortune, 2, 2);
        painel.addItem(area, 4, 2);
        painel.addItem(experiente, 6, 2);

        painel.addItem(machadoGui, 4, 0);

        menu.getPanes().clear();
        menu.addPane(painel);
        menu.update();
    }


    public GuiItem setupFortune() {
        int fortune = tool.getEnchantment(EnchantmentList.FORTUNE, machado);
        int custo = tool.getPrice(EnchantmentList.FORTUNE, fortune);

        ItemStack fortuneItem = new ItemStack(Material.LAPIS_LAZULI);
        ItemMeta fortuneMeta = fortuneItem.getItemMeta();
        Color color = new Color(46, 147, 229);

        assert fortuneMeta != null;
        fortuneMeta.setDisplayName(net.md_5.bungee.api.ChatColor.of(color) + "Fortuna §l" + fortune);
        ArrayList<String> list = new ArrayList<>();
        list.add("§7Aumente a quantidade de");
        list.add("§7lucro de sua colheita.");
        list.add("");
        if(fortune < 20) {
            list.add("  §FNível: " + net.md_5.bungee.api.ChatColor.of(color) + fortune + "/20");
            list.add("  §fCusto: §6✪" + Base.formatNumber(custo));
            list.add("");
            if(economyAPI.getPlayerCurrency(player.getUniqueId(), CurrencyType.cash) >= custo) {
                list.add(net.md_5.bungee.api.ChatColor.of(color) + "Clique aqui para dar o upgrade.");
            } else {
                list.add("§cVocê não possui cash suficiente.");
            }
        } else {
            list.add("  §FNível: " + net.md_5.bungee.api.ChatColor.of(color) + "20/20");
            list.add("");
            list.add("§cVocê está no nível máximo.");
        }

        fortuneMeta.setLore(list);
        fortuneItem.setItemMeta(fortuneMeta);

        return new GuiItem(fortuneItem, e -> {
            e.setCancelled(true);
            if(fortune < 20) {
                if(economyAPI.getPlayerCurrency(player.getUniqueId(), CurrencyType.cash) >= custo) {
                    tool.setEnchantment(EnchantmentList.FORTUNE, machado, fortune + 1, player);
                    economyAPI.removeCurrency(player.getUniqueId(), CurrencyType.cash, custo);
                } else {
                    player.sendMessage("§cVocê não possui cash suficiente.");
                }
                updateMenu();
            }

        });

    }

    public GuiItem setupArea() {
        int nivel = tool.getEnchantment(EnchantmentList.AREA_BREAK, machado);
        int custo = tool.getPrice(EnchantmentList.AREA_BREAK, nivel);

        ItemStack fortuneItem = new ItemStack(Material.END_CRYSTAL);
        ItemMeta fortuneMeta = fortuneItem.getItemMeta();

        Color color = new Color(177, 22, 26);

        assert fortuneMeta != null;
        fortuneMeta.setDisplayName(net.md_5.bungee.api.ChatColor.of(color) + "Quebra-Área §l" + nivel);
        ArrayList<String> list = new ArrayList<>();
        list.add("§7Tenha chance de quebrar");
        list.add("§7uma área §f3x3§7 ao colher.");
        if(nivel < 100) {
            list.add("");
            list.add("  §FNível: " + net.md_5.bungee.api.ChatColor.of(color) + nivel + "/100");
            list.add("  §fCusto: §6✪" + Base.formatNumber(custo));
            list.add("");
            if(economyAPI.getPlayerCurrency(player.getUniqueId(), CurrencyType.cash) >= custo) {
                list.add(net.md_5.bungee.api.ChatColor.of(color) + "Clique aqui para dar o upgrade.");
            } else {
                list.add("§cVocê não possui cash suficiente.");
            }
        } else {
            list.add("");
            list.add("  §FNível: " + net.md_5.bungee.api.ChatColor.of(color) + "100/100");
            list.add("");
            list.add("§cVocê está no nível máximo.");
        }

        fortuneMeta.setLore(list);
        fortuneItem.setItemMeta(fortuneMeta);

        return new GuiItem(fortuneItem, e -> {
            e.setCancelled(true);
            if(nivel < 100) {
                if(economyAPI.getPlayerCurrency(player.getUniqueId(), CurrencyType.cash) >= custo) {
                    tool.setEnchantment(EnchantmentList.AREA_BREAK, machado, nivel + 1, player);
                    economyAPI.removeCurrency(player.getUniqueId(), CurrencyType.cash, custo);
                } else {
                    player.sendMessage("§cVocê não possui cash suficiente.");
                }
                updateMenu();
            }

        });

    }

    public GuiItem setupExperiente() {
        int nivel = tool.getEnchantment(EnchantmentList.EXPERIENTE, machado);
        int custo = tool.getPrice(EnchantmentList.EXPERIENTE, nivel);

        ItemStack fortuneItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta fortuneMeta = fortuneItem.getItemMeta();

        Color color = new Color(78, 245, 89);

        assert fortuneMeta != null;
        fortuneMeta.setDisplayName(net.md_5.bungee.api.ChatColor.of(color) + "Experiente §l" + nivel);
        ArrayList<String> list = new ArrayList<>();
        list.add("§7Ganhe mais xp ao colher");
        list.add("§7suas plantações.");
        if(nivel < 100) {
            list.add("");
            list.add("  §FNível: " + net.md_5.bungee.api.ChatColor.of(color) + nivel + "/50");
            list.add("  §fCusto: §6✪" + Base.formatNumber(custo));
            list.add("");
            if(economyAPI.getPlayerCurrency(player.getUniqueId(), CurrencyType.cash) >= custo) {
                list.add(net.md_5.bungee.api.ChatColor.of(color) + "Clique aqui para dar o upgrade.");
            } else {
                list.add("§cVocê não possui cash suficiente.");
            }
        } else {
            list.add("");
            list.add("  §FNível: " + net.md_5.bungee.api.ChatColor.of(color) + "50/50");
            list.add("");
            list.add("§cVocê está no nível máximo.");
        }

        fortuneMeta.setLore(list);
        fortuneItem.setItemMeta(fortuneMeta);

        return new GuiItem(fortuneItem, e -> {
            e.setCancelled(true);
            if(nivel < 100) {
                if(economyAPI.getPlayerCurrency(player.getUniqueId(), CurrencyType.cash) >= custo) {
                    tool.setEnchantment(EnchantmentList.EXPERIENTE, machado, nivel + 1, player);
                    economyAPI.removeCurrency(player.getUniqueId(), CurrencyType.cash, custo);
                } else {
                    player.sendMessage("§cVocê não possui cash suficiente.");
                }
                updateMenu();
            }

        });

    }

}
