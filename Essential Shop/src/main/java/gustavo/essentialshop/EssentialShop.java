package gustavo.essentialshop;

import gustavo.essentialeconomy.EssentialEconomy;
import gustavo.essentialeconomy.Manager.EconomyManager;
import gustavo.essentialshop.Shop.ShopManager;
import gustavo.essentialshop.manager.FileManager;
import gustavo.essentialshop.manager.Manager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EssentialShop extends JavaPlugin {

    private ShopManager shopManager;
    Manager manager;
    private EconomyManager economyAPI;

    @Override
    public void onEnable() {

        EssentialEconomy essentialEconomy = (EssentialEconomy) getServer().getPluginManager().getPlugin("EssentialEconomy");
        if (essentialEconomy == null) {
            getLogger().severe("EssentialEconomy plugin não encontrado!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.economyAPI = essentialEconomy.getEconomyManager();

        FileManager fileManager = new FileManager(this);
        this.shopManager = new ShopManager(this, fileManager);
        shopManager.loadCategories();
        manager = new Manager(this);
        manager.register();

    }

    public ShopManager getShopManager() {
        return shopManager;
    }
    public EconomyManager getEconomyAPI() {
        return economyAPI;
    }

    @Override
    public void onDisable() {
        if(shopManager != null) {
            shopManager.saveCategories();
        }
    }
}
