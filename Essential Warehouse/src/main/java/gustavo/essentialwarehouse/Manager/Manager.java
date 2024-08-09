package gustavo.essentialwarehouse.Manager;

import com.plotsquared.core.PlotAPI;
import gustavo.essentialeconomy.Manager.EconomyManager;
import gustavo.essentialwarehouse.EssentialWarehouse;
import gustavo.essentialwarehouse.PlotManager.Base;
import gustavo.essentialwarehouse.PlotManager.DropListener;
import gustavo.essentialwarehouse.PlotManager.RemoveListener;
import gustavo.essentialwarehouse.SpecialTool.listener.BlockBreakEvent;
import gustavo.essentialwarehouse.SpecialTool.listener.InteractEvent;
import gustavo.essentialwarehouse.Warehouse.commands.WarehouseCommand;
import gustavo.essentialwarehouse.SpecialTool.commands.giveTool;

public class Manager {

    private final EssentialWarehouse plugin;

    public Manager(EssentialWarehouse essentialWarehouse) {
        this.plugin = essentialWarehouse;
    }

    public void Registro() {

        EconomyManager economyManager = plugin.getEconomyAPI();
        Base base = new Base(plugin, economyManager);
        new WarehouseCommand(base);
        new giveTool();

        plugin.getServer().getPluginManager().registerEvents(new DropListener(base), plugin);
        plugin.getServer().getPluginManager().registerEvents(new BlockBreakEvent(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new InteractEvent(), plugin);

        PlotAPI api = new PlotAPI();
        api.registerListener(new RemoveListener(base));

    }

}
