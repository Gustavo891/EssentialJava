package gustavo.essentialmines.manager;

import gustavo.essentialeconomy.Manager.EconomyManager;
import gustavo.essentialmines.EssentialMines;
import gustavo.essentialmines.Mine.MineManager;
import gustavo.essentialmines.Mine.OresManager;
import gustavo.essentialmines.commands.InfoCommand;
import gustavo.essentialmines.commands.MinesCommand;
import gustavo.essentialmines.commands.ResetCommand;
import gustavo.essentialmines.commands.SetupCommand;
import gustavo.essentialmines.listeners.BlockBreak;

public class Manager {

    private final EssentialMines plugin;
    private final MineManager mineManager;
    private final OresManager oresManager;
    private final EconomyManager economyAPI;

    public Manager(EssentialMines plugin) {
        this.plugin = plugin;
        this.mineManager = plugin.getMineManagerInstance(); // Cria uma nova instância de MineManager aqui
        this.oresManager = plugin.getOresManagerInstance();
        this.economyAPI = plugin.getEconomyAPI();
    }

    public void register() {
        new InfoCommand(mineManager);
        new ResetCommand(mineManager);
        new SetupCommand(plugin.getFileManager(), mineManager);
        new MinesCommand(mineManager, plugin);

        plugin.getLogger().info("Comandos registrados com sucesso.");
        plugin.getLogger().info("MineManager passado para comandos: " + (mineManager != null));

        plugin.getServer().getPluginManager().registerEvents(new BlockBreak(mineManager, oresManager, economyAPI), plugin);
        plugin.getServer().getPluginManager().registerEvents(plugin.getMineManagerInstance(), plugin);
    }
}
