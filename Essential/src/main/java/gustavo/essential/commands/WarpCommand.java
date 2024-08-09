package gustavo.essential.commands;

import gustavo.essential.manager.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Objects;

public class WarpCommand extends Command {

    private final FileManager fileManager;

    public WarpCommand(FileManager fileManager) {
        super(
                "warp",
                "Ir para um local definido.",
                "§cUse /warp (local).",
                new String[]{""},
                ""
        );
        this.fileManager = fileManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
        } else {
            Player player = (Player) sender;
            FileConfiguration config = fileManager.getFile();

            if (args.length == 1) {
                teleportToWarp(player, config, args[0]);
            } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                if(player.hasPermission("essential.setwarp")) {
                    setWarp(player, config, args[1]);
                } else {
                    player.sendMessage("§cVocê não tem permissão para executar esse comando.");
                }
            } else {
                player.sendMessage("Uso correto: /warp <nome> ou /warp set <nome>");
            }
        }

    }

    private void teleportToWarp(Player player, FileConfiguration config, String warpName) {
        if (!config.contains("warps." + warpName)) {
            player.sendMessage("§cWarp " + warpName + " não existe.");
            return;
        }
        String worldName = config.getString("warps." + warpName + ".world");
        assert worldName != null;
        Location warpLocation = new Location(
                Bukkit.getWorld(worldName),
                config.getDouble("warps." + warpName + ".x"),
                config.getDouble("warps." + warpName + ".y"),
                config.getDouble("warps." + warpName + ".z"),
                (float) config.getDouble("warps." + warpName + ".yaw"),
                (float) config.getDouble("warps." + warpName + ".pitch")
        );
        player.teleport(warpLocation);
        player.sendMessage("§eTeleportado para a warp " + warpName + ".");
    }

    private void setWarp(Player player, FileConfiguration config, String warpName) {
        Location location = player.getLocation();
        config.set("warps." + warpName + ".world", Objects.requireNonNull(location.getWorld()).getName());
        config.set("warps." + warpName + ".x", location.getX());
        config.set("warps." + warpName + ".y", location.getY());
        config.set("warps." + warpName + ".z", location.getZ());
        config.set("warps." + warpName + ".yaw", location.getYaw());
        config.set("warps." + warpName + ".pitch", location.getPitch());
        fileManager.saveLocaisConfig();
        player.sendMessage("§aWarp " + warpName + " definida com sucesso.");
    }

}
