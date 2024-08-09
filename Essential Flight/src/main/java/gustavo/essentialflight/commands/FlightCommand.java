package gustavo.essentialflight.commands;

import gustavo.essentialflight.EssentialFlight;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class FlightCommand extends Command{

    private final EssentialFlight plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final HashMap<UUID, Long> flightEndTimes = new HashMap<>();
    private final FlightGUI flightGUI;

    public FlightCommand(EssentialFlight plugin, FlightGUI flightGUI) {
        super(
                "flight",
                "Comando para voar",
                "§cUse /voar.",
                new String[]{"voar", "fly"},
                ""
        );
        this.plugin = plugin;
        this.flightGUI = flightGUI;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }
        if(args.length == 0) {
            if(player.hasPermission("essentials.vip.voar")) {
                if(player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.sendMessage("§cO seu modo de voo foi desativado.");
                } else {
                    player.setAllowFlight(true);
                    player.sendMessage("§aO seu modo de voo foi ativado.");
                }
            } else {
                flightGUI.openMenu(player);
            }
        } else if (args.length == 1 && player.isOp()) {
            if (Objects.equals(args[0], "menu")) {
                flightGUI.openMenu(player);
            } else {
                player.sendMessage("§cUse /voar menu para abrir o menu manualmente.");
            }
        } else {
            player.sendMessage("§cUse /voar para gerenciar o seu modo de voo.");
        }

        return true;
    }


}
