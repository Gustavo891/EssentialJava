package gustavo.essential.commands.vault;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.mongodb.client.MongoDatabase;
import gustavo.essential.Essential;
import gustavo.essentialeconomy.Manager.CurrencyType;
import gustavo.essentialeconomy.Manager.EconomyManager;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Map;

public class VaultGUI {

    int preco = 200000;
    private final Player player;
    private final MongoDatabase database;
    private final VaultManager vaultManager;
    EconomyManager economy;

    public VaultGUI(Essential plugin, Player player) {
        this.player = player;
        database = plugin.getMongoDBManager().getDatabase();
        vaultManager = new VaultManager(plugin);
        economy = plugin.getEconomyAPI();

    }

    public void openBaus() {
        ChestGui baus = new ChestGui(4, "Seus baús:");

        ArrayList<GuiItem> itens = new ArrayList<>();
        listChests(player, itens);

        StaticPane painel = new StaticPane(1,3,7,1);
        configPainel(painel);

        if(itens.isEmpty()) {
            StaticPane bau = getBau();
            baus.addPane(bau);
        } else {
            PaginatedPane bau = new PaginatedPane(1, 1, 7, 1);
            bau.populateWithGuiItems(itens);
            baus.addPane(bau);
        }
        baus.addPane(painel);
        baus.show(player);

    }

    private void configPainel(StaticPane painel) {

        ItemStack comprar = new ItemStack(Material.GOLD_INGOT);
        ItemMeta comprarMeta = comprar.getItemMeta();
        assert comprarMeta != null;
        comprarMeta.setDisplayName("§eAdquirir um baú");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§r");
        lore.add("  §7Preço: §2$§f200k    §r");
        lore.add("§r");
        if(vaultManager.checkSize(player)) {
            lore.add("§cVocê já possui o máximo de baús.");
        } else {
            if(economy.getPlayerCurrency(player.getUniqueId(), CurrencyType.money) >= preco) {
                lore.add("§aClique aqui para adquiri-lo.");
            } else {
                lore.add("§cVocê não possui dinheiro suficiente.");
            }
        }
        comprarMeta.setLore(lore);
        comprar.setItemMeta(comprarMeta);
        GuiItem comprarGui = new GuiItem(comprar, event -> {
            event.setCancelled(true);
            if(economy.getPlayerCurrency(player.getUniqueId(), CurrencyType.money) >= preco) {
                vaultManager.buyChest(player);
            } else {
                lore.add("§cVocê não possui dinheiro suficiente.");
            }
            event.getWhoClicked().closeInventory();
        });

        painel.addItem(comprarGui, 3, 0);

    }

    private StaticPane getBau() {
        StaticPane bau = new StaticPane(1, 1, 7, 1);
        ItemStack vazio = new ItemStack(Material.COBWEB);
        ItemMeta meta = vazio.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§CNenhum");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Adquira um bau para aparecer aqui.");
        meta.setLore(lore);
        vazio.setItemMeta(meta);
        GuiItem item = new GuiItem(vazio, event -> {
            event.setCancelled(true);
        });
        bau.addItem(item, 3, 0);
        return bau;
    }

    private void listChests(Player player, ArrayList<GuiItem> itens) {
        Document document = database.getCollection("chests").find(new Document("uuid", player.getUniqueId().toString())).first();
        if (document != null) {

            for (Map.Entry<String, Object> entry : document.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("bau_")) { // Filtrar baús pelo prefixo
                    Document chestDocument = (Document) entry.getValue();
                    itens.add(getGuiItem(chestDocument));
                }
            }

        } else {
            player.sendMessage("§cVocê não tem baús registrados.");
        }
    }

    private GuiItem getGuiItem(Document bau) {

        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(" §7Tamanho: §f" + bau.getInteger("size"));
        lore.add("");
        lore.add("§eClique para acessa-lo.");

        Material item = Material.CHEST;
        ItemStack bauItem = new ItemStack(item);

        ItemMeta mineMeta = bauItem.getItemMeta();
        assert mineMeta != null;
        mineMeta.setDisplayName(ChatColor.of("#789AF3") + bau.getString("name"));
        mineMeta.setLore(lore);
        bauItem.setItemMeta(mineMeta);

        return new GuiItem(bauItem, event -> {

            vaultManager.openChest(bau, player, player.getUniqueId());
            event.setCancelled(true);
        });
    }



}
