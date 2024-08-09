package gustavo.essentialmines.Mine;

import gustavo.essentialmines.EssentialMines;
import gustavo.essentialmines.manager.FileManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OresManager {

    private final EssentialMines plugin;
    private final FileManager fileManager;
    private final List<Ores> ores = new ArrayList<>();

    public OresManager(EssentialMines plugin) {
        this.plugin = plugin;
        this.fileManager = plugin.getOresManager();
        loadOres();
    }

    public void loadOres() {
        FileConfiguration config = fileManager.getFile();
        if (config == null) {
            plugin.getLogger().severe("Configuração de mines não foi carregada.");
            return;
        }

        if (!config.contains("ores")) {
            plugin.getLogger().warning("Nenhuma mina encontrada no arquivo de configuração.");
            return;
        }

        for (String key : config.getConfigurationSection("ores").getKeys(false)) {
            int price = config.getInt("ores." + key + ".money");

            Material material;
            try {
                material = Material.valueOf(key);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().severe("Material inválido: " + key);
                continue;
            }

            Ores ore = new Ores(material, price);
            ores.add(ore);
        }
    }


    public void saveOres() {
        FileConfiguration config = fileManager.getFile();
        for (Ores ore : ores) {
            String path = "ores." + ore.getType().toString() + ".money";
            config.set(path, ore.getPrice());
        }
        fileManager.saveLocaisConfig();
    }


    public boolean addOre(Material type, int price) {
        for(Ores ore : ores) {
            if(ore.getType() == type) {
                ore.setPrice(price);
                return false;
            }
        }
        Ores ore = new Ores(type, price);
        ores.add(ore);
        return true;
    }

    public List<Ores> getOres() {
        return ores;
    }


}
