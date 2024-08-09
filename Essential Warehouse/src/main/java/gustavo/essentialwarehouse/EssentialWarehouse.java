package gustavo.essentialwarehouse;

import com.plotsquared.core.PlotAPI;
import gustavo.essentialeconomy.EssentialEconomy;
import gustavo.essentialeconomy.Manager.EconomyManager;
import gustavo.essentialwarehouse.Manager.Manager;
import gustavo.essentialwarehouse.Manager.MongoDBManager;
import gustavo.essentialwarehouse.PlotManager.RemoveListener;
import gustavo.essentialwarehouse.SpecialTool.ToolItem;
import org.bukkit.plugin.java.JavaPlugin;

public final class EssentialWarehouse extends JavaPlugin {

    Manager manager;
    private MongoDBManager mongoDBManager;
    private EconomyManager economyAPI;
    private ToolItem toolItem;

    @Override
    public void onEnable() {
        toolItem = new ToolItem(this);
        EssentialEconomy essentialEconomy = (EssentialEconomy) getServer().getPluginManager().getPlugin("EssentialEconomy");
        assert essentialEconomy != null;
        economyAPI = essentialEconomy.getEconomyManager();
        mongoDBManager = new MongoDBManager();
        mongoDBManager.connect();
        manager = new Manager(this);
        manager.Registro();

    }
    public MongoDBManager getMongoDBManager() {
        return mongoDBManager;
    }
    public static EssentialWarehouse getInstance() {
        return getPlugin(EssentialWarehouse.class);
    }

    public ToolItem getToolItem() {
        return toolItem;
    }
    public EconomyManager getEconomyAPI() { return economyAPI; }
    @Override
    public void onDisable() {
    }


}
