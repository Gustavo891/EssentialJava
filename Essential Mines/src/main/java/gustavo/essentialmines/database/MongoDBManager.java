package gustavo.essentialmines.database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import gustavo.essentialmines.EssentialMines;
import org.bson.Document;

public class MongoDBManager {
    private final EssentialMines plugin;
    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoDBManager(EssentialMines plugin) {
        this.plugin = plugin;
    }

    String host = "localhost";
    int port = 27017;

    public void connect() {
        mongoClient = new MongoClient(host, port);
        database = mongoClient.getDatabase ("server");
    }

    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public MongoCollection<Document> getPunishmentsCollection() {
        return database.getCollection("punishments");
    }
}