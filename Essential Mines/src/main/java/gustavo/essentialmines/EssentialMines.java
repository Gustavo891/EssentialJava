package gustavo.essentialmines;

import gustavo.essentialeconomy.EssentialEconomy;
import gustavo.essentialeconomy.Manager.EconomyManager;
import gustavo.essentialmines.Mine.OresManager;
import gustavo.essentialmines.manager.Manager;
import gustavo.essentialmines.database.MongoDBManager;
import gustavo.essentialmines.manager.FileManager;
import gustavo.essentialmines.Mine.MineManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EssentialMines extends JavaPlugin {

    private FileManager mineManager, oresManager;
    private MongoDBManager mongoDBManager;
    private MineManager mineManagerInstance;
    private OresManager oresManagerInstance;
    private EconomyManager economyAPI;

    @Override
    public void onEnable() {
        mongoDBManager = new MongoDBManager(this);
        mongoDBManager.connect();

        this.mineManager = new FileManager(this, "mines");
        this.oresManager = new FileManager(this, "ores");

        EssentialEconomy essentialEconomy = (EssentialEconomy) getServer().getPluginManager().getPlugin("EssentialEconomy");
        if (essentialEconomy == null) {
            getLogger().severe("EssentialEconomy plugin não encontrado!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.economyAPI = essentialEconomy.getEconomyManager();

        mineManager.saveFile();
        oresManager.saveFile();

        this.mineManagerInstance = new MineManager(this);
        this.oresManagerInstance = new OresManager(this);

        Manager manager = new Manager(this);
        manager.register();

        getLogger().info("EssentialMines habilitado com sucesso.");
        getLogger().info("FileManager inicializado: " + (mineManager != null));
        getLogger().info("MineManager inicializado: " + (mineManagerInstance != null));
    }

    public FileManager getFileManager() {
        return mineManager;
    }

    public FileManager getOresManager() {
        return oresManager;
    }

    public EconomyManager getEconomyAPI() {
        return economyAPI;
    }

    public MineManager getMineManagerInstance() {
        return mineManagerInstance;
    }

    public OresManager getOresManagerInstance() {
        return oresManagerInstance;
    }

    public MongoDBManager getMongoDBManager() {
        return mongoDBManager;
    }

    @Override
    public void onDisable() {
        mineManager.saveFile();
        oresManager.saveFile();
        mineManagerInstance.saveMines();
        oresManagerInstance.saveOres();
        mongoDBManager.disconnect();
    }
}
