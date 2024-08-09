package gustavo.essentialmines.commands;

import gustavo.essentialmines.EssentialMines;
import gustavo.essentialmines.Mine.MineManager;
import gustavo.essentialmines.commands.gui.listMines;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MinesCommand extends Command{

    private final MineManager mineManager;
    private final EssentialMines plugin;

    public MinesCommand(MineManager mineManager, EssentialMines plugin) {
        super(
                "minas",
                "Veja as minas existentes do servidor",
                "§cUse /minas.",
                new String[]{},
                "essential.minas"
        );
        this.mineManager = mineManager;
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }

        listMines listMines = new listMines(3, "Minas:", plugin, mineManager);
        listMines.show(player);

        return false;
    }
}
