package gustavo.essentialmines.commands;

import gustavo.essentialmines.Mine.Mine;
import gustavo.essentialmines.Mine.MineManager;
import org.bukkit.command.CommandSender;

public class InfoCommand extends Command {

    private final MineManager mineManager;

    public InfoCommand(MineManager mineManager) {
        super(
                "minainfo",
                "Mostra as informações da mina",
                "§cUse /minainfo {tipo}.",
                new String[]{},
                "essential.minainfo"
        );
        this.mineManager = mineManager;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            for(Mine mine : mineManager.getMines().values()) {
                sender.sendMessage("§a" + mine.getInfo());
            }
        }

        if (args.length != 1) {
            sender.sendMessage("§cUso correto: /minainfo {tipo}");
            return false;
        }

        String tipo = args[0];
        Mine mine = mineManager.getMine(tipo);


        if (mine == null) {
            sender.sendMessage("§cMina não encontrada!");
            return false;
        }

        sender.sendMessage("§e" + mine.getInfo());

        return true;
    }
}
