package gustavo.essentialmines.commands.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import gustavo.essentialmines.EssentialMines;
import gustavo.essentialmines.Mine.Mine;
import gustavo.essentialmines.Mine.MineManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class listMines extends ChestGui {

    public listMines(int rows, String title, EssentialMines plugin, MineManager mineManager) {
        super(rows, title, plugin);

        PaginatedPane pages = new PaginatedPane(1, 1, 7, 2);
        Map<String, Mine> mines = mineManager.getMines();

        ArrayList<GuiItem> itens = new ArrayList<>();

        for (Map.Entry<String, Mine> entry : mines.entrySet()) {
            GuiItem guiItem = getGuiItem(entry.getValue());
            itens.add(guiItem);
        }

        pages.populateWithGuiItems(itens);
        addPane(pages);

        StaticPane navigation = new StaticPane(0, 4, 9, 1);
        navigation.addItem(new GuiItem(new ItemStack(Material.RED_WOOL), event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
                update();
            }
            event.setCancelled(true);
        }), 1, 0);

        navigation.addItem(new GuiItem(new ItemStack(Material.GREEN_WOOL), event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
                update();
            }
            event.setCancelled(true);
        }), 7, 0);

        addPane(navigation);

    }


    private GuiItem getGuiItem(Mine mine) {

        String name = mine.getName();
        String oreType = null;
        Map<String, Integer> ores = mine.getOres();
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("  §fComposição:");
        for (Map.Entry<String, Integer> entry : ores.entrySet()) {
            if(oreType == null) {
                oreType = entry.getKey();
            }
            lore.add("  §8➟ §7" + entry.getKey().replace('_', ' '));
        }
        lore.add("");
        lore.add("§eClique para teleportar.");

        Material item = Material.valueOf(oreType);
        ItemStack bloco = new ItemStack(item);

        ItemMeta mineMeta = bloco.getItemMeta();
        assert mineMeta != null;
        mineMeta.setDisplayName(ChatColor.of("#789AF3") + "§l" + name.toUpperCase());
        mineMeta.setLore(lore);
        bloco.setItemMeta(mineMeta);

        return new GuiItem(bloco, event -> {
            event.getWhoClicked().sendMessage("§aVocê foi teletransportado para a mina: §f" + mine.getName());
            event.getWhoClicked().teleport(mine.getSpawn());
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        });
    }

}
