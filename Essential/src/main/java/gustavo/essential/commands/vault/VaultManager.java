package gustavo.essential.commands.vault;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import gustavo.essential.Essential;
import gustavo.essentialeconomy.Manager.CurrencyType;
import gustavo.essentialeconomy.Manager.EconomyManager;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class VaultManager implements Listener {

    int preco = 200000;
    private final Map<UUID, String> openChests = new HashMap<>();
    private final Map<UUID, UUID> chestOwners = new HashMap<>();
    private final MongoDatabase database;
    private final Map<UUID, String> nomeBau = new HashMap<>();
    EconomyManager economyAPI;

    public VaultManager(@NotNull Essential plugin) {
        database = plugin.getMongoDBManager().getDatabase();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        economyAPI = plugin.getEconomyAPI();
    }
    public void buyChest(Player player) {
        player.sendMessage("§r");
        player.sendMessage("  §eEscreva o nome do seu baú:");
        player.sendMessage("  §7Máximo: §f10 caracteres");
        player.sendMessage("§r");
        nomeBau.put(player.getUniqueId(), "nomeBau");
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String name = event.getMessage().trim();

        if (nomeBau.containsKey(player.getUniqueId()) &&
                nomeBau.get(player.getUniqueId()).equals("nomeBau")) {

            event.setCancelled(true);

            if (name.equalsIgnoreCase("cancelar")) {
                nomeBau.remove(player.getUniqueId());
                player.sendMessage("§cProcesso cancelado.");
                return;
            }

            if (!name.isEmpty() && name.length() <= 10) {
                nomeBau.remove(player.getUniqueId());
                finishBuy(player, name);
            } else {
                player.sendMessage("§cO nome do baú deve ter entre 1 e 10 caracteres.");
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (openChests.containsKey(player.getUniqueId())) {
            String chestName = openChests.get(player.getUniqueId());
            UUID ownerUUID = chestOwners.get(player.getUniqueId());
            saveItemsToDocument(player, event.getInventory(), chestName, ownerUUID);
            openChests.remove(player.getUniqueId()); // Remove from tracking map
            chestOwners.remove(player.getUniqueId()); // Remove owner tracking
        }
    }

    public void finishBuy(Player player, String name) {
        if(economyAPI.getPlayerCurrency(player.getUniqueId(), CurrencyType.money) > preco) {
            int info = createChest(name, 9, player);
            switch (info) {
                case 0:
                    player.sendMessage("§aBau adquirido com sucesso.");
                    break;
                case 1:
                    player.sendMessage("§cVocê já possui um baú com esse nome.");
                    break;
                case 2:
                    player.sendMessage("§cVocê já possui o máximo de baús.");
                    break;
            }
        } else {
            player.sendMessage("§cVocê não possui dinheiro suficiente.");
        }
    }
    public void giveChest(String name, int size, @NotNull Player player, Player sender) {

        int info = createChest(name, size, player);
        switch (info) {
            case 0:
                sender.sendMessage("§aO bau foi entregue com sucesso.");
                break;
            case 1:
                sender.sendMessage("§cJá existe um baú com esse nome.");
                break;
            case 2:
                sender.sendMessage("§cO jogador já possui o máximo de baús.");
                break;
        }

    }
    private int createChest(String name, int size, @NotNull Player player) {
        Document document = database.getCollection("chests")
                .find(new Document("uuid", player.getUniqueId().toString()))
                .first();
        if (document == null) {
            document = new Document("uuid", player.getUniqueId().toString());
        }
        if (document.containsKey("bau_" + name)) {
            return 1; // ja existe
        }
        if(checkSize(player)) {
            return 2;
        }

        Document chest = new Document("name", name)
                .append("size", size)
                .append("items", new HashMap<String, String>());

        document.append("bau_" + name, chest);
        database.getCollection("chests")
                .replaceOne(new Document("uuid", player.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));

        return 0; // bau criado com sucesso

    }
    public boolean checkSize(Player player) {
        Document document = database.getCollection("chests")
                .find(new Document("uuid", player.getUniqueId().toString()))
                .first();
        if(document == null) {
            return false;
        }
        long quantidadeBaus = document.keySet().stream()
                .filter(key -> key.startsWith("bau_"))
                .count();
        return quantidadeBaus >= 7; //maximo de baus
    }
    public void deleteChest(String name, @NotNull Player player, Player sender) {
        Document document = database.getCollection("chests")
                .find(new Document("uuid", player.getUniqueId().toString()))
                .first();

        if (document == null) {
            sender.sendMessage("§cEsse jogador não possui nenhum baú.");
        }

        assert document != null;
        if (document.containsKey("bau_" + name)) {
            document.remove("bau_" + name);
            database.getCollection("chests")
                    .replaceOne(new Document("uuid", player.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
            sender.sendMessage("§cO bau §f'" + name + "'§c foi removido.");
            return;
        } else {
            sender.sendMessage("§cO jogador não possui um bau com esse nome.");
        }
    }
    public void listChests(@NotNull Player player, Player sender) {
        Document document = database.getCollection("chests")
                .find(new Document("uuid", player.getUniqueId().toString()))
                .first();

        if (document == null) {
            sender.sendMessage("§cO jogador não possui nenhum baú.");
            return;
        }

        Set<String> keys = document.keySet();
        StringBuilder chestList = new StringBuilder("§aSeus baús:");

        for (String key : keys) {
            if (key.startsWith("bau_")) {
                String chestName = key.substring(4); // Remove "bau_" prefix
                Document chestDocument = (Document) document.get(key);
                int size = chestDocument.getInteger("size", 27);
                chestList.append("\n§6").append(chestName).append(" §7(Tamanho: ").append(size).append(")");
            }
        }

        if (chestList.length() == "§aSeus baús:".length()) {
            sender.sendMessage("§cVocê não possui nenhum baú.");
        } else {
            sender.sendMessage(chestList.toString());
        }
    }
    public void openChest(Document bau, Player player, UUID ownerUUID) {
        if (bau != null) {
            int size = bau.getInteger("size", 54);
            Inventory chest = Bukkit.createInventory(player, size, "Baú: " + bau.getString("name"));
            loadItemsFromDocument(chest, bau);
            player.openInventory(chest);
            openChests.put(player.getUniqueId(), bau.getString("name"));
            chestOwners.put(player.getUniqueId(), ownerUUID);
        } else {
            player.sendMessage("§cBaú não encontrado.");
        }
    }
    public void openPlayerChest(Player target, Player admin, String name) {
        Document document = database.getCollection("chests").find(new Document("uuid", target.getUniqueId().toString())).first();
        if (document != null) {
            for (Map.Entry<String, Object> entry : document.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("bau_" + name)) { // Filtrar baús pelo prefixo
                    Document bau = (Document) entry.getValue();
                    if(bau != null) {
                        openChest(bau, admin, target.getUniqueId());
                    }
                }
            }
        }
    }
    private void loadItemsFromDocument(Inventory chest, Document chestDocument) {
        if (chestDocument != null) {
            Map<String, String> items = (Map<String, String>) chestDocument.get("items");
            if (items != null) {
                for (String key : items.keySet()) {
                    int slot = Integer.parseInt(key);
                    String serializedItem = items.get(key);
                    if (serializedItem != null) {
                        chest.setItem(slot, deserializeItem(serializedItem));
                    }
                }
            }
        }
    }
    public void saveItemsToDocument(@NotNull Player player, Inventory chest, String chestName, UUID ownerUUID) {
        Document document = database.getCollection("chests").find(new Document("uuid", ownerUUID.toString())).first();
        if (document != null) {
            Document chestDocument = (Document) document.get("bau_" + chestName);
            if (chestDocument == null) {
                chestDocument = new Document("name", chestName)
                        .append("size", chest.getSize())
                        .append("items", new HashMap<String, String>());
            }

            Map<String, String> items = new HashMap<>();
            for (int i = 0; i < chest.getSize(); i++) {
                ItemStack item = chest.getItem(i);
                if (item != null) {
                    items.put(String.valueOf(i), serializeItem(item));
                }
            }
            chestDocument.put("items", items);

            document.put("bau_" + chestName, chestDocument);
            database.getCollection("chests").replaceOne(new Document("uuid", ownerUUID.toString()), document, new ReplaceOptions().upsert(true));
        }
    }

    private @Nullable String serializeItem(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private @Nullable ItemStack deserializeItem(String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
