package gustavo.essentialshop.manager;

import gustavo.essentialshop.EssentialShop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {
    private final EssentialShop plugin;

    public FileManager(EssentialShop plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration loadFile(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName + ".yaml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Cria o arquivo se não existir
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(fileName + ".yaml", false);
        }

        return config;
    }

    public void saveFile(String fileName, FileConfiguration config) {
        File file = new File(plugin.getDataFolder(), fileName + ".yaml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
