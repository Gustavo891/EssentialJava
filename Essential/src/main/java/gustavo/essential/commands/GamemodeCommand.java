package gustavo.essential.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class GamemodeCommand extends Command {

    public GamemodeCommand() {
        super(
                "gamemode",
                "Trocar o modo de jogo",
                "§cUse /gamemode (modo).",
                new String[]{"gm"},
                "essential.gamemode"
        );
    }



    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
        } else {
            switch(args.length) {
                case 1:
                    setGamemode((Player) sender, (Player) sender, args);
                    break;
                case 2:
                    Player player = Bukkit.getPlayer(args[1]);

                    if(player != null && player.isOnline()) {
                        setGamemode((Player) sender, player, args);
                    } else {
                        sender.sendMessage("§cJogador não encontrado ou offline.");
                    }
                    break;
                default:
                    sender.sendMessage("§cUse /gamemode (modo de jogo) (jogador).");
                    break;
            }
        }

    }

    private void setGamemode(Player sender, Player target, String[] args) {
        if(Objects.equals(args[0], "0") || args[0].equalsIgnoreCase("survival")) {
            target.setGameMode(GameMode.SURVIVAL);
            sender.sendMessage("§eO modo de jogo de §f" + target.getDisplayName() + " §efoi alterado para §fSOBREVIVENCIA§e.");
        } else if (Objects.equals(args[0], "1") || args[0].equalsIgnoreCase("creative")) {
            target.setGameMode(GameMode.CREATIVE);
            sender.sendMessage("§eO modo de jogo de §f" + target.getDisplayName() + " §efoi alterado para §fCRIATIVO§e.");
        } else if (Objects.equals(args[0], "2") || args[0].equalsIgnoreCase("adventure")) {
            target.setGameMode(GameMode.ADVENTURE);
            sender.sendMessage("§eO modo de jogo de §f" + target.getDisplayName() + " §efoi alterado para §fAVENTURA§e.");
        } else if (Objects.equals(args[0], "3") || args[0].equalsIgnoreCase("spectator")) {
            target.setGameMode(GameMode.SPECTATOR);
            sender.sendMessage("§eO modo de jogo de §f" + target.getDisplayName() + " §efoi alterado para §fESPECTADOR§e.");
        }
    }
}
