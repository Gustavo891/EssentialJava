package gustavo.essentialwarehouse.Warehouse.commands;

import com.plotsquared.core.plot.Plot;
import gustavo.essentialwarehouse.PlotManager.Base;
import gustavo.essentialwarehouse.Warehouse.gui.WarehouseGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class WarehouseCommand extends Command{

    Base base;
    WarehouseGUI warehouseGUI;

    public WarehouseCommand(Base base) {
        super(
                "warehouse",
                "Comando para abrir seu armazem.",
                "§cUse /warehouse.",
                new String[]{"armazem"},
                ""
        );
        this.base = base;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player player)) {
            return false;
        }
        Plot plot = base.getPlot(player.getLocation());
        String id = plot.getId().toString();
        if(plot == null) {
            player.sendMessage("§cVocê precisa executar esse comando em uma plot claimada.");
            return false;
        }
        if(args.length == 0) {
            if(plot.getTrusted().contains(player.getUniqueId()) || Objects.equals(plot.getOwner(), player.getUniqueId())) {
                warehouseGUI = new WarehouseGUI(base, player, plot);
                warehouseGUI.openMenu();
            } else {
                player.sendMessage("§cVocê não tem permissão para acessar esse armazém.");
            }
        }
        if(args.length == 1) {
            if(Objects.equals(plot.getOwner(), player.getUniqueId())) {
                if(args[0].equalsIgnoreCase("privado")) {
                    if(base.getPrivate(id)) {
                        base.setPrivate(id, false);
                        player.sendMessage("§aAgora todos membros do terreno podem vender os produtos do armazém.");
                    } else {
                        base.setPrivate(id, true);
                        player.sendMessage("§aAgora somente o dono do terreno pode vender os produtos do armazém.");
                    }
                } else {
                    player.sendMessage("§cComando inválido. Use /armazem privado.");
                }
            } else {
                player.sendMessage("§cApenas o dono do terreno pode usar esse comando.");
            }
        } else if(args.length > 1) {
            player.sendMessage("§cComando inválido. Use /armazem privado ou /armazem.");
        }

        return false;
    }
}
