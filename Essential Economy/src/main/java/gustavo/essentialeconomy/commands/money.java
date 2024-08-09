package gustavo.essentialeconomy.commands;

import gustavo.essentialeconomy.Manager.CurrencyType;
import gustavo.essentialeconomy.Manager.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static gustavo.essentialeconomy.PAPI.PAPIManager.formatNumber;

public class money extends Command {

    private final EconomyManager economyManager;

    public money(EconomyManager economyManager) {
        super(
                "money",
                "Comando sobre o seu dinheiro",
                "§cUse /money.",
                new String[]{},
                "essential.money"

        );
        this.economyManager = economyManager;

    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Esse comando só pode ser executado por um jogador.");
            return true;
        }

        int money = economyManager.getPlayerCurrency(player.getUniqueId(), CurrencyType.money);

        if (args.length == 0) {
            player.sendMessage("§aVocê possui §2$§f" + formatNumber(money) + "§a de money.");
            return true;
        } else if(args.length == 3) {
            switch (args[0]) {
                case "pay":
                    try {
                        int amount = Integer.parseInt(args[2]);
                        if(money >= amount) {
                            Player target = Bukkit.getPlayer(args[1]);
                            if(target != null && target.isOnline()) {
                                economyManager.removeCurrency(player.getUniqueId(), CurrencyType.money, amount);
                                economyManager.addCurrency(target.getUniqueId(), CurrencyType.money, amount);
                                target.sendMessage("§aVocê recebeu §2$§f" + formatNumber(amount) + " §ade §7" + player.getDisplayName() + "§a.");
                                player.sendMessage("§aVocê enviou §2$§f" + formatNumber(amount) + " §apara §7" + target.getDisplayName() + "§a.");
                            } else {
                                player.sendMessage("§cO jogador está offline ou é inexistente.");
                            }
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cInsira um número válido.");
                    }
                    break;
                default:
                    player.sendMessage("§aComando inválido. Tente novamente.");
                    break;
            }
        } else if(args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null && target.isOnline()) {
                player.sendMessage("§aO jogador §7" + target.getDisplayName() + "§a possui §2$§f" + formatNumber(economyManager.getPlayerCurrency(target.getUniqueId(), CurrencyType.money)) + "§a.");
            } else {
                player.sendMessage("§cO jogador está offline ou é inexistente.");
            }
        } else {
            player.sendMessage("§aComando inválido. Tente novamente.");
        }







        return false;
    }

}
