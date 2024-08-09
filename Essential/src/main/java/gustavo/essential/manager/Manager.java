package gustavo.essential.manager;

import gustavo.essential.Essential;
import gustavo.essential.FastBoard.Scoreboard;
import gustavo.essential.commands.*;
import gustavo.essential.commands.vault.VaultCommand;
import gustavo.essential.listeners.Damage;

public class Manager {

    private final Essential plugin;

    public Manager(Essential essential) {
        this.plugin = essential;
    }

    public void register() {
        FileManager locaisManager = plugin.getLocaisManager();
        FileManager configManager = plugin.getConfigManager();


        new SpawnCommand(locaisManager);
        new WarpCommand(locaisManager);
        new GamemodeCommand();
        new AlertCommand(configManager);
        new RepairCommand();
        VaultCommand openCommand = new VaultCommand(plugin);

        plugin.getServer().getPluginManager().registerEvents(new SpawnCommand(locaisManager), plugin);
        plugin.getServer().getPluginManager().registerEvents(new Scoreboard(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new Damage(), plugin);

    }
}
