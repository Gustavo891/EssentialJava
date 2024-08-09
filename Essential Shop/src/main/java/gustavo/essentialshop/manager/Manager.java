package gustavo.essentialshop.manager;

import gustavo.essentialshop.EssentialShop;
import gustavo.essentialshop.commands.ShopCommand;

public class Manager {

    private final EssentialShop plugin;

    public Manager(EssentialShop plugin) {
        this.plugin = plugin;
    }

    public void register() {

        new ShopCommand(plugin);

    }
}
