package gustavo.essentialeconomy.events;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import gustavo.essentialeconomy.EssentialEconomy;
import gustavo.essentialeconomy.Manager.CurrencyType;
import org.bson.Document;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class onJoin implements Listener {
    private final EssentialEconomy plugin;

    public onJoin(EssentialEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        MongoCollection<Document> collection = plugin.getCollection();

        if (collection != null) {
            Document document = collection.find(Filters.eq("uuid", playerUUID.toString())).first();
            if (document == null) {
                Document newPlayer = new Document("uuid", playerUUID.toString())
                        .append(String.valueOf(CurrencyType.money), 0)
                        .append(String.valueOf(CurrencyType.cash), 0);
                collection.insertOne(newPlayer);
                event.getPlayer().sendMessage("Bem-vindo! Sua conta foi criada.");
            } else {
                event.getPlayer().sendMessage("Bem-vindo de volta!");
            }
        } else {
            event.getPlayer().sendMessage("Erro ao acessar a base de dados.");
        }
    }
}