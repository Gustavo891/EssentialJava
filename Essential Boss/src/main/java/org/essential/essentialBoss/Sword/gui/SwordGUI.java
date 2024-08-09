package org.essential.essentialBoss.Sword.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import gustavo.essentialeconomy.Manager.CurrencyType;
import gustavo.essentialeconomy.Manager.EconomyManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.essential.essentialBoss.EssentialBoss;
import org.essential.essentialBoss.Sword.SwordItem;

import java.awt.*;
import java.util.ArrayList;

public class SwordGUI {

    private final Player player;
    private final ItemStack item;
    private final SwordItem swordItem;
    private final EconomyManager economyAPI = EssentialBoss.getInstance().getEconomyAPI();
    ChestGui gui = new ChestGui(3, "Matadora:");

    public SwordGUI(Player player, ItemStack item, SwordItem swordItem) {
        this.player = player;
        this.item = item;
        this.swordItem = swordItem;
    }

    public void open() {
        StaticPane painel = new StaticPane(0, 0,9,3);
        GuiItem dano = setupDano();

        painel.addItem(dano, 2, 1);
        gui.addPane(painel);
        gui.show(player);
    }

    public GuiItem setupDano() {
        int dano = swordItem.getDano(item);
        int custo = swordItem.getPrice("dano", dano + 1);

        ItemStack fortuneItem = new ItemStack(Material.REDSTONE);
        ItemMeta fortuneMeta = fortuneItem.getItemMeta();
        Color color = new Color(153, 47, 47);

        assert fortuneMeta != null;
        fortuneMeta.setDisplayName(net.md_5.bungee.api.ChatColor.of(color) + "Dano §l" + dano);
        ArrayList<String> list = new ArrayList<>();
        list.add("§7Melhore o dano que sua");
        list.add("§7matadora aplica no boss.");
        list.add("");
        list.add("  §FNível: " + net.md_5.bungee.api.ChatColor.of(color) + dano);
        list.add("  §fCusto: §2$§f" + SwordItem.formatNumber(custo));
        list.add("");
        if(economyAPI.getPlayerCurrency(player.getUniqueId(), CurrencyType.money) >= custo) {
            list.add(net.md_5.bungee.api.ChatColor.of(color) + "Clique aqui para dar o upgrade.");
        } else {
            list.add("§cVocê não possui dinheiro suficiente.");
        }


        fortuneMeta.setLore(list);
        fortuneItem.setItemMeta(fortuneMeta);

        return new GuiItem(fortuneItem, e -> {
            e.setCancelled(true);
                if(economyAPI.getPlayerCurrency(player.getUniqueId(), CurrencyType.money) >= custo) {
                    swordItem.setEnchantment("dano", dano, item);
                    economyAPI.removeCurrency(player.getUniqueId(), CurrencyType.money, custo);
                    player.sendMessage("§aVocê melhorou o §lDano§r§a da matadora para §f" + (dano + 1) + "§a.");
                } else {
                    player.sendMessage("§cVocê não possui dinheiro suficiente.");
                }
                updateMenu();


        });

    }

    private void updateMenu() {
        StaticPane painel = new StaticPane(0, 0,9,3);
        GuiItem dano = setupDano();
        painel.addItem(dano, 2, 1);

        gui.getPanes().clear();
        gui.addPane(painel);
        gui.update();
    }

}
