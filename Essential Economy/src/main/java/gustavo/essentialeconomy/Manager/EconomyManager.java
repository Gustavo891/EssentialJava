package gustavo.essentialeconomy.Manager;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.UUID;

public class EconomyManager {
    private final MongoCollection<Document> playersCollection;

    public EconomyManager(MongoCollection<Document> playersCollection) {
        this.playersCollection = playersCollection;
    }

    public void addCurrency(UUID uuid, CurrencyType currencyType, int quantidade) {
        updateCurrency(uuid, currencyType, quantidade);
    }

    public boolean removeCurrency(UUID uuid, CurrencyType currencyType, int quantidade) {
        updateCurrency(uuid, currencyType, -quantidade);
        return true;

    }

    public void setCurrency(UUID uuid, CurrencyType currencyType, int quantidade) {
        Document filter = new Document("uuid", uuid.toString());
            Document update = new Document("$set", new Document(currencyType.toString(), quantidade));
        playersCollection.updateOne(filter, update);
    }

    public int getPlayerCurrency(UUID uuid, CurrencyType currencyType) {
        Document playerData = playersCollection.find(new Document("uuid", uuid.toString())).first();

        if (playerData != null) {
            return playerData.getInteger(currencyType.toString(), 0);
        }
        return 0;
    }

    private void updateCurrency(UUID uuid, CurrencyType currencyType, int quantidade) {
        Document filter = new Document("uuid", uuid.toString());
        Document update = new Document("$inc", new Document(currencyType.toString(), quantidade));
        playersCollection.updateOne(filter, update);
    }

}

