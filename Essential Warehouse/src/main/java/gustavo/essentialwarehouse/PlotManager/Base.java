package gustavo.essentialwarehouse.PlotManager;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import gustavo.essentialeconomy.Manager.CurrencyType;
import gustavo.essentialeconomy.Manager.EconomyManager;
import gustavo.essentialwarehouse.EssentialWarehouse;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class Base {

    MongoDatabase database;
    private EconomyManager economyManager;

    Map<Material, String> ITEMS = Map.of(
            Material.MELON_SLICE, "Melancia",
            Material.CARROT, "Cenoura",
            Material.POTATO, "Batata",
            Material.CACTUS, "Cactu",
            Material.WHEAT, "Trigo",
            Material.NETHER_WART, "Fungo do Nether",
            Material.PUMPKIN, "Abôbora"
    );
    Map<Material, Integer> PRICES = Map.of(
            Material.MELON_SLICE, 10,
            Material.CARROT, 25,
            Material.POTATO, 25,
            Material.CACTUS, 5,
            Material.WHEAT, 20,
            Material.NETHER_WART, 15,
            Material.PUMPKIN, 10
    );

    public Base(EssentialWarehouse plugin, EconomyManager economyManager) {
        database = plugin.getMongoDBManager().getDatabase();
        this.economyManager = economyManager;
    }

    public Map<Material, String> getLista() {
        return ITEMS;
    }
    public Map<Material, Integer> getPRICES() {
        return PRICES;
    }
    public String getName(Material material) {

        for(Map.Entry<Material, String> entry : ITEMS.entrySet()) {
            if(entry.getKey() == material) {
                return entry.getValue();
            }
        }

        return null;
    }
    public boolean checkId(String id) {

        Document document = database.getCollection("armazem")
                .find(new Document("id", id))
                .first();
        if (document == null) {
            document = new Document("id", id);
        }

        database.getCollection("armazem")
                .replaceOne(new Document("id", id), document, new ReplaceOptions().upsert(true));

        return true;
    }
    public void saveData(String id, String stringValue, int intValue) {
        Document query = new Document("id", id);
        Document update = new Document("$inc", new Document(stringValue, intValue));

        database.getCollection("armazem").updateOne(query, update);

    }
    public void removeData(String id, String nome, int quantidade) {
        Document query = new Document("id", id);
        Document update = new Document("$unset", new Document(nome, ""));

        database.getCollection("armazem").updateOne(query, update);
    }
    public Plot getPlot(Location loc) {
        com.plotsquared.core.location.Location plotLoc = BukkitUtil.adapt(loc);
        PlotArea plotArea = PlotSquared.get().getPlotAreaManager().getPlotArea(plotLoc);

        if (plotArea == null) {
            return null;
        }
        Plot plot = plotArea.getPlot(plotLoc);
        if (plot == null || plot.getOwner() == null) {
            return null;
        }
        return plot;
    }
    public Map<Material, Integer> getItemQuantities(String id) {
        Document document = database.getCollection("armazem")
                .find(new Document("id", id))
                .first();

        Map<Material, Integer> itemQuantities = new HashMap<>();
        if (document != null) {
            for (Map.Entry<String, Object> entry : document.entrySet()) {
                String key = entry.getKey();
                Material material = getMaterialFromKey(key);

                if (material != null) {
                    int quantity = (int) entry.getValue();
                    itemQuantities.put(material, quantity);
                }
            }
        } else {
            checkId(id);
        }
        return itemQuantities;
    }
    private Material getMaterialFromKey(String key) {
        try {
            Material material = Material.valueOf(key);
            if (ITEMS.containsKey(material)) {
                return material;
            }
        } catch (IllegalArgumentException ignored) {
        }
        return null;
    }
    public int getQuantidade(Material material, String id) {
        Document document = database.getCollection("armazem")
                .find(new Document("id", id))
                .first();
        if (document != null) {
            for (Map.Entry<String, Object> entry : document.entrySet()) {
                String key = entry.getKey();
                if(Objects.equals(key, material.toString())) {
                    return (int) entry.getValue();
                }
            }
        }
        return -1;
    }
    public void sellProduct(Material material, int quantidade, String id, Player player) {
        int price = PRICES.get(material);
        String nome = ITEMS.get(material);
        if(price > 0 && nome != null) {
            economyManager.addCurrency(player.getUniqueId(), CurrencyType.money, price * quantidade);
            player.sendMessage("§aVocê vendeu §f" + nome + " §apelo valor de §2$§f" + formatNumber(price * quantidade) + "§a!");
            removeData(id, material.toString(), quantidade);
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
    public boolean getPrivate(String id) {
        Document document = database.getCollection("armazem")
                .find(new Document("id", id))
                .first();

        if (document != null) {
            if (document.containsKey("private") && document.get("private") instanceof Boolean) {
                return document.getBoolean("private");
            } else {
                Document update = new Document("$set", new Document("private", false));
                database.getCollection("armazem").updateOne(document, update);
                return false;
            }
        } else {
            Document newDocument = new Document("id", id)
                    .append("private", false);
            database.getCollection("armazem").insertOne(newDocument);
            return false;
        }
    }
    public void setPrivate(String id, boolean value) {
        Document query = new Document("id", id);
        Document update = new Document("$set", new Document("private", value));

        database.getCollection("armazem").updateOne(query, update);
    }

    public void deleteId(String id) {
        Document query = new Document("id", id);
        database.getCollection("armazem").deleteOne(query);
    }


}


