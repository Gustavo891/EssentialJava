package gustavo.essential.manager;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;

public class MongoDBManager {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    String host = "localhost";
    int port = 27017;

    public void connect() {
        try {
            mongoClient = new MongoClient(host, port);
            database = mongoClient.getDatabase("server");
        } catch (Exception e) {
            Bukkit.getLogger().severe("Erro ao conectar ao MongoDB: " + e.getMessage());
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
