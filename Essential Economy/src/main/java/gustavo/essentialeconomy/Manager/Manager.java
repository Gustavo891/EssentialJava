package gustavo.essentialeconomy.Manager;


import gustavo.essentialeconomy.EssentialEconomy;
import gustavo.essentialeconomy.commands.money;
import gustavo.essentialeconomy.events.onJoin;

public class Manager {

    private final EssentialEconomy plugin;

    public Manager(EssentialEconomy plugin) {
        this.plugin = plugin;
    }

    public void register() {

        EconomyManager economyManager = plugin.getEconomyManager();
        new money(economyManager);

        plugin.getServer().getPluginManager().registerEvents(new onJoin(plugin), plugin);


    }
}
