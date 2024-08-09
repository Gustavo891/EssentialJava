package gustavo.essentialwarehouse.Warehouse.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import gustavo.essentialwarehouse.PlotManager.Base;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class WarehouseGUI {

    Base base;
    private final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4"); // We reuse the same "random" UUID all the time
    Player player;
    private ChestGui armazem = new ChestGui(5, "Armazem:");
    private final Plot plot;

    public WarehouseGUI(Base base, Player player, Plot plot) {
        this.base = base;
        this.player = player;
        this.plot = plot;
    }

    public void openMenu() {
        StaticPane painel = new StaticPane(1, 1, 7, 2);
        StaticPane nav = new StaticPane(0, 4, 9, 1);
        loadProducts(painel);
        configNav(nav);
        armazem.addPane(painel);
        armazem.show(player);
    }

    public void configNav(StaticPane nav) {
        ItemStack sair = new ItemStack(Material.ARROW);
        ItemMeta sairMeta = sair.getItemMeta();
        assert sairMeta != null;
        sairMeta.setDisplayName("§cFechar");
        sair.setItemMeta(sairMeta);

        GuiItem sairGui = new GuiItem(sair, event -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        });

        nav.addItem(sairGui, 0, 0);

        PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID);
        PlayerTextures textures = profile.getTextures();
        ItemStack level1 = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta level1Meta = (SkullMeta) level1.getItemMeta();
        URL urlObject;
        try {
            urlObject = new URL("https://textures.minecraft.net/texture/cbb632ceb83e2c39cb53e801a29af9109cf341df89d01ed8dfeb49460a5264c8");
        } catch (MalformedURLException exception) {
            level1.setType(Material.NETHER_STAR);
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject);

        assert level1Meta != null;
        level1Meta.setDisplayName("§6Modo Privado");
        List<String> lores = new ArrayList<>();
        lores.add("§7Configure para apenas o dono do terreno");
        lores.add("§7conseguir vender os produtos do armazém.");
        lores.add("");
        if(base.getPrivate(plot.getId().toString())) {
            lores.add("  §fStatus: §aAtivado");
        } else {
            lores.add("  §fStatus: §cDesativado");
        }
        lores.add("§r");
        lores.add("§eClique para alterar o modo.");
        level1Meta.setLore(lores);
        level1.setItemMeta(level1Meta);

        GuiItem modoPrivate = new GuiItem(level1, event -> {
            event.setCancelled(true);
            if(base.getPrivate(plot.getId().toString())) {
                base.setPrivate(plot.getId().toString(), false);
            } else {
                base.setPrivate(plot.getId().toString(), true);
            }
            updateMenu();
        });

        nav.addItem(modoPrivate, 4, 0);
        armazem.addPane(nav);

    }

    public void loadProducts(StaticPane painel) {
        Map<Material, Integer> itemQuantities = base.getItemQuantities(plot.getId().toString());
        painel.clear(); // Limpar o painel antes de recarregar os itens
        if (itemQuantities.isEmpty()) {
            GuiItem item = loadEmpty();
            painel.addItem(item, 3, 0);
        } else {
            int counter = 0;
            for (Map.Entry<Material, Integer> entry : itemQuantities.entrySet()) {
                GuiItem product = loadItem(entry.getKey(), entry.getValue(), painel);
                painel.addItem(product, counter, 0);
                counter++;
            }
        }
    }

    private GuiItem loadEmpty() {
        ItemStack empty = new ItemStack(Material.COBWEB);
        ItemMeta emptyItemMeta = empty.getItemMeta();
        assert emptyItemMeta != null;
        emptyItemMeta.setDisplayName("§cVázio");
        List<String> lores = new ArrayList<>();
        lores.add("§7Nenhuma plantação por aqui...");
        emptyItemMeta.setLore(lores);
        empty.setItemMeta(emptyItemMeta);

        return new GuiItem(empty, e -> e.setCancelled(true));
    }

    private GuiItem loadItem(Material material, int quantidade, StaticPane pane) {
        int total = 0;
        for(Map.Entry<Material, Integer> entry : base.getPRICES().entrySet()) {
            if(entry.getKey() == material) {
                total = quantidade * entry.getValue();
            }
        }
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§a" + base.getName(material));
        List<String> lores = new ArrayList<>();
        lores.add("");
        lores.add("  §fQuantidade: §7" + base.formatNumber(quantidade));
        lores.add("  §fValor total: §2$§f" + base.formatNumber(total));
        lores.add("");
        if(base.getPrivate(plot.getId().toString())) {
            if(plot.getOwner().equals(player.getUniqueId())) {
                lores.add("§eClique para vender.");
            } else {
                lores.add("§cVocê não tem permissão para vender.");
            }
        } else {
            lores.add("§eClique para vender.");
        }
        itemMeta.setLore(lores);
        item.setItemMeta(itemMeta);

        return new GuiItem(item, e -> {
            e.setCancelled(true);
            if(base.getPrivate(plot.getId().toString()) && Objects.equals(plot.getOwner(), player.getUniqueId()) || !base.getPrivate(plot.getId().toString())) {
                int quantAtual = base.getQuantidade(material, plot.getId().toString());
                if (quantAtual > 0) {
                    base.sellProduct(material, quantAtual, plot.getId().toString(), player);
                    updateMenu();
                } else {
                    player.sendMessage("§cVocê não possui nenhuma plantação para vender. Alguém já deve ter vendido!");
                }
            }
        });
    }

    private void updateMenu() {
        StaticPane newPane = new StaticPane(1, 1, 7, 2);
        StaticPane nav = new StaticPane(0, 4, 9, 1);
        armazem.getPanes().clear();
        loadProducts(newPane);
        configNav(nav);
        armazem.addPane(newPane);
        armazem.addPane(nav);
        armazem.update();
    }
}
