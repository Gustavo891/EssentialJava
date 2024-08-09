package gustavo.essentialflight;

import gustavo.essentialeconomy.EssentialEconomy;
import gustavo.essentialeconomy.Manager.EconomyManager;
import gustavo.essentialflight.manager.Manager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EssentialFlight extends JavaPlugin {

    Manager manager;
    private EconomyManager economyAPI;

    @Override
    public void onEnable() {

        getLogger().info("Plugin sendo iniciado.");
        EssentialEconomy essentialEconomy = (EssentialEconomy) getServer().getPluginManager().getPlugin("EssentialEconomy");
        if (essentialEconomy == null) {
            getLogger().severe("EssentialEconomy plugin não encontrado!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.economyAPI = essentialEconomy.getEconomyManager();

        manager = new Manager(this);
        manager.register();

    }

    public EconomyManager getEconomyAPI() {
        return economyAPI;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
