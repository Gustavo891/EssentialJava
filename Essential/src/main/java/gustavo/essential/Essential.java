package gustavo.essential;

import gustavo.essential.manager.Manager;
import gustavo.essential.manager.FileManager;
import gustavo.essential.manager.MongoDBManager;
import gustavo.essentialeconomy.EssentialEconomy;
import gustavo.essentialeconomy.Manager.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class Essential extends JavaPlugin {

    private FileManager locaisManager, configManager;
    private static MongoDBManager mongoDBManager;
    private EconomyManager economyAPI;


    @Override
    public void onEnable() {

        mongoDBManager = new MongoDBManager();
        mongoDBManager.connect();

        EssentialEconomy essentialEconomy = (EssentialEconomy) getServer().getPluginManager().getPlugin("EssentialEconomy");
        if (essentialEconomy == null) {
            getLogger().severe("EssentialEconomy plugin não encontrado!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.economyAPI = essentialEconomy.getEconomyManager();

        this.locaisManager = new FileManager(this, "locais");
        this.configManager = new FileManager(this, "config");
        locaisManager.saveFile();
        configManager.saveFile();

        Manager manager = new Manager(this);
        manager.register();

    }

    public FileManager getLocaisManager() {
        return locaisManager;
    }
    public FileManager getConfigManager() {
        return configManager;
    }
    public MongoDBManager getMongoDBManager() {
        return mongoDBManager;
    }
    public EconomyManager getEconomyAPI() {
        return economyAPI;
    }
    @Override
    public void onDisable() {
        mongoDBManager.disconnect();
    }
}
