package gustavo.essential.commands;

import gustavo.essential.manager.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnCommand extends Command implements Listener {

    private final FileManager fileManager;

    public SpawnCommand(FileManager fileManager) {
        super(
                "spawn",
                "Ir para o spawn",
                "§cUse /spawn.",
                new String[]{""},
                ""
        );
        this.fileManager = fileManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
        } else {
            Player player = (Player) sender;
            FileConfiguration config = fileManager.getFile();

            if (args.length == 0) {
                irSpawn(player, config);
            } else if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
                if(player.hasPermission("essential.setspawn")) {
                    setarSpawn(player, config);
                } else {
                    player.sendMessage("§cVocê não tem permissão para executar esse comando.");
                }
            } else {
                player.sendMessage("§cUso correto: /spawn.");
            }
        }

    }

    private void irSpawn(Player player, FileConfiguration config) {
        String worldName = config.getString("spawn.world");
        if (worldName == null || worldName.isEmpty()) {
            player.sendMessage("§cO spawn não foi definido. Consulte um administrador.");
            return;
        }
        player.teleport(getLocation(config));
        player.sendMessage("§eTeleportado para o spawn.");
    }

    private void setarSpawn(Player player, FileConfiguration config) {
        Location location = player.getLocation();
        config.set("spawn.world", location.getWorld().getName());
        config.set("spawn.x", location.getX());
        config.set("spawn.y", location.getY());
        config.set("spawn.z", location.getZ());
        config.set("spawn.yaw", location.getYaw());
        config.set("spawn.pitch", location.getPitch());
        fileManager.saveLocaisConfig();
        player.sendMessage("§aSpawn definido com sucesso.");
        location.getWorld().setSpawnLocation(location);
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        irSpawn(e.getPlayer(), fileManager.getFile());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        e.setQuitMessage(null);
    }

    @EventHandler
    public void onSpawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(getLocation(fileManager.getFile()));
    }

    public Location getLocation(FileConfiguration config) {
        String worldName = config.getString("spawn.world");
        return new Location(
                Bukkit.getWorld(worldName),
                config.getDouble("spawn.x"),
                config.getDouble("spawn.y"),
                config.getDouble("spawn.z"),
                (float) config.getDouble("spawn.yaw"),
                (float) config.getDouble("spawn.pitch")
        );
    }
}
