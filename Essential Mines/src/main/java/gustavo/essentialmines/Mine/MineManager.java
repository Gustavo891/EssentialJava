package gustavo.essentialmines.Mine;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import gustavo.essentialmines.EssentialMines;
import gustavo.essentialmines.manager.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class MineManager implements Listener {

    private final EssentialMines plugin;
    private final FileManager fileManager;
    private final Map<String, Mine> mines = new HashMap<>();
    Hologram holograma;

    public MineManager(EssentialMines plugin) {
        this.plugin = plugin;
        this.fileManager = plugin.getFileManager(); // Usando a instância do FileManager fornecida pelo EssentialMines
        loadMines();
    }

    public void loadMines() {
        FileConfiguration config = fileManager.getFile();
        if (config == null) {
            plugin.getLogger().severe("Configuração de mines não foi carregada.");
            return;
        }

        if (!config.contains("mines")) {
            plugin.getLogger().warning("Nenhuma mina encontrada no arquivo de configuração.");
            return;
        }

        for (String key : config.getConfigurationSection("mines").getKeys(false)) {
            String worldName1 = config.getString("mines." + key + ".pos1.world");
            String worldName2 = config.getString("mines." + key + ".pos2.world");

            if (worldName1 == null || worldName2 == null) {
                plugin.getLogger().warning("Nome do mundo nulo para a mina: " + key);
                continue;
            }

            World world1 = Bukkit.getWorld(worldName1);
            World world2 = Bukkit.getWorld(worldName2);

            if (world1 == null || world2 == null) {
                plugin.getLogger().warning("Mundo não encontrado para a mina: " + key);
                continue;
            }

            Location pos1 = new Location(
                    world1,
                    config.getDouble("mines." + key + ".pos1.x"),
                    config.getDouble("mines." + key + ".pos1.y"),
                    config.getDouble("mines." + key + ".pos1.z"),
                    (float) config.getDouble("mines." + key + ".pos1.yaw"),
                    (float) config.getDouble("mines." + key + ".pos1.pitch")
            );
            Location pos2 = new Location(
                    world2,
                    config.getDouble("mines." + key + ".pos2.x"),
                    config.getDouble("mines." + key + ".pos2.y"),
                    config.getDouble("mines." + key + ".pos2.z"),
                    (float) config.getDouble("mines." + key + ".pos2.yaw"),
                    (float) config.getDouble("mines." + key + ".pos2.pitch")
            );
            Location spawn = new Location(
                    world2,
                    config.getDouble("mines." + key + ".spawn.x"),
                    config.getDouble("mines." + key + ".spawn.y"),
                    config.getDouble("mines." + key + ".spawn.z"),
                    (float) config.getDouble("mines." + key + ".spawn.yaw"),
                    (float) config.getDouble("mines." + key + ".spawn.pitch")
            );

            Map<String, Integer> ores = new HashMap<>();
            List<String> oreEntries = config.getStringList("mines." + key + ".ores");
            for (String entry : oreEntries) {
                String[] parts = entry.split(", ");
                if (parts.length == 2) {
                    String oreType = parts[0];
                    int chance;
                    try {
                        chance = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        // Log or handle the error appropriately
                        continue;
                    }
                    ores.put(oreType, chance);
                }
            }

            Mine mine = new Mine(pos1, pos2, key, ores, spawn);
            mines.put(key, mine);
            mine.setupHolo();
            plugin.getLogger().info("Mina carregada: " + key);
            plugin.getLogger().info("Pos1: " + pos1);
            plugin.getLogger().info("Pos2: " + pos2);
        }
    }

    public void saveMines() {
        FileConfiguration config = fileManager.getFile();
        for (Map.Entry<String, Mine> entry : mines.entrySet()) {
            String key = entry.getKey();
            Mine mine = entry.getValue();

            String minePath = "mines." + key;
            Location pos1 = mine.getPos1();
            Location pos2 = mine.getPos2();
            Location spawn = mine.getSpawn();

            config.set(minePath + ".pos1.world", Objects.requireNonNull(pos1.getWorld()).getName());
            config.set(minePath + ".pos1.x", pos1.getX());
            config.set(minePath + ".pos1.y", pos1.getY());
            config.set(minePath + ".pos1.z", pos1.getZ());
            config.set(minePath + ".pos1.yaw", pos1.getYaw());
            config.set(minePath + ".pos1.pitch", pos1.getPitch());

            config.set(minePath + ".pos2.world", Objects.requireNonNull(pos2.getWorld()).getName());
            config.set(minePath + ".pos2.x", pos2.getX());
            config.set(minePath + ".pos2.y", pos2.getY());
            config.set(minePath + ".pos2.z", pos2.getZ());
            config.set(minePath + ".pos2.yaw", pos2.getYaw());
            config.set(minePath + ".pos2.pitch", pos2.getPitch());

            config.set(minePath + ".spawn.world", Objects.requireNonNull(spawn.getWorld()).getName());
            config.set(minePath + ".spawn.x", spawn.getX());
            config.set(minePath + ".spawn.y", spawn.getY());
            config.set(minePath + ".spawn.z", spawn.getZ());
            config.set(minePath + ".spawn.yaw", spawn.getYaw());
            config.set(minePath + ".spawn.pitch", spawn.getPitch());

            List<String> oreList = new ArrayList<>();
            for (Map.Entry<String, Integer> ore : mine.getOres().entrySet()) {
                oreList.add(ore.getKey() + ", " + ore.getValue());
            }

            config.set(minePath + ".ores", oreList);
        }

        fileManager.saveLocaisConfig();
    }

    public void createMine(String key, Location pos1, Location pos2, Map<String, Integer> ores, Location spawn) {
        mines.put(key, new Mine(pos1, pos2, key, ores, spawn));
        saveMines();
    }

    public void deleteMine(String key) {
        mines.remove(key);
        FileConfiguration config = fileManager.getFile();
        config.set("mines." + key, null);
        fileManager.saveLocaisConfig();
    }

    public Mine getMine(String key) {
        return mines.get(key);
    }

    public Map<String, Mine> getMines() {
        return mines;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        for (Map.Entry<String, Mine> entry : mines.entrySet()) {
            Mine mina = entry.getValue();
            if(mina.getCuboid().contains(e.getBlock().getLocation())) {
                if(mina.getProgress() > 80) {
                    mina.reset();
                    Bukkit.broadcastMessage("§aA mina §f" + entry.getKey() + " §afoi resetada. Aproveite!");
                }
                holograma = DHAPI.getHologram("mina" + entry.getKey());
                assert holograma != null;
                DHAPI.setHologramLine(holograma, 4, mina.barraProgresso());

            }
        }
    }


}
