package gustavo.essential.manager;

import gustavo.essential.Essential;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {
    private final Essential plugin;
    private FileConfiguration locaisConfig;
    private File locaisFile;
    private String file;

    public FileManager(Essential plugin, String file) {
        this.plugin = plugin;
        this.file = file;
        createFile();
    }

    private void createFile() {
        locaisFile = new File(plugin.getDataFolder(), file + ".yaml");
        if (!locaisFile.exists()) {
            locaisFile.getParentFile().mkdirs();
            plugin.saveResource(file + ".yaml", false);
        }

        locaisConfig = YamlConfiguration.loadConfiguration(locaisFile);
    }

    public void saveFile() {
        if (!locaisFile.exists()) {
            plugin.saveResource(file + ".yaml", false);
        }
    }

    public FileConfiguration getFile() {
        return locaisConfig;
    }

    public void saveLocaisConfig() {
        try {
            locaisConfig.save(locaisFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
