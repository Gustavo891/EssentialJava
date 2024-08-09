package gustavo.essentialmines.commands;

import gustavo.essentialmines.Mine.Mine;
import gustavo.essentialmines.Mine.MineManager;
import org.bukkit.command.CommandSender;

public class ResetCommand extends Command {

    private final MineManager mineManager;

    public ResetCommand(MineManager mineManager) {
        super(
                "minareset",
                "Reseta a mina",
                "§cUse /minareset {tipo}.",
                new String[]{},
                "essential.minareset"
        );
        this.mineManager = mineManager;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§cUso correto: /minareset {tipo}");
            return false;
        }

        String tipo = args[0];
        Mine mine = mineManager.getMine(tipo);

        if (mine == null) {
            sender.sendMessage("§cMina não encontrada!");
            return false;
        }

        mine.reset();
        sender.sendMessage("§aMina §7(" + tipo + ") §afoi resetada com sucesso!");

        return true;
    }
}
