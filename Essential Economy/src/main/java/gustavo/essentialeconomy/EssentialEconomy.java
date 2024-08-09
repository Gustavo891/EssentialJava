package gustavo.essentialeconomy;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import gustavo.essentialeconomy.Manager.EconomyManager;
import gustavo.essentialeconomy.PAPI.PAPIManager;
import gustavo.essentialeconomy.Manager.Manager;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class EssentialEconomy extends JavaPlugin {


    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private EconomyManager economyManager;


    @Override
    public void onEnable() {

        String host = "localhost";
        int port = 27017;

        Manager manager = new Manager(this);

        try {
            mongoClient = new MongoClient(host, port);
            database = mongoClient.getDatabase("server");
            collection = database.getCollection("players");
            economyManager = new EconomyManager(collection);

        } catch (Exception e) {
            Bukkit.getLogger().severe("Erro ao conectar ao MongoDB: " + e.getMessage());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new PAPIManager(economyManager).register(); //
            getLogger().info("PLACEHOLDER FOI CARREGADO.");
        } else {
            getLogger().info("PLACEHOLDER API NÃO FOI CARREGADO.");
        }

        manager.register();

    }

    @Override
    public void onDisable() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }
}
