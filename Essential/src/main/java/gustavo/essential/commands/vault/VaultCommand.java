package gustavo.essential.commands.vault;

import com.mongodb.client.MongoDatabase;
import gustavo.essential.Essential;
import gustavo.essential.commands.Command;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VaultCommand extends Command {

    private Essential plugin;

    public VaultCommand(Essential plugin) {
        super(
                "vault",
                "Abra um bau privado.",
                "§cUse /vaults.",
                new String[]{"baus", "bau", "vaults"},
                "essential.vaults"
        );
        this.plugin = plugin;
    }


    @Override
    public void execute(CommandSender sender, String[] args) {

        VaultManager vaultManager = new VaultManager(plugin);
        if (sender instanceof Player player) {

            if (args.length == 0) {
                VaultGUI vaultGUI = new VaultGUI(plugin, player);
                vaultGUI.openBaus();
                return;
            } else {
                if(player.hasPermission("essentials.vault.admin")) {
                    switch(args[0]) {
                        case "dar": // vault dar {player} {tamanho} {nome}
                            if(args.length == 4) {
                                Player target = Bukkit.getPlayer(args[1]);
                                if(target != null) {
                                    vaultManager.giveChest(args[3], Integer.parseInt(args[2]), target, player);
                                } else {
                                    player.sendMessage("§cEsse jogador não existe ou não está online.");
                                }
                            } else {
                                player.sendMessage("§cUtilize /vault dar {jogador} {tamanho} {nome}");
                            }
                            break;
                        case "remover": // vault remover {player} {nome}
                            if(args.length == 3) {
                                Player target = Bukkit.getPlayer(args[1]);
                                if(target != null) {
                                    vaultManager.deleteChest(args[2], target, player);
                                } else {
                                    player.sendMessage("§cEsse jogador não existe ou não está online.");
                                }
                            } else {
                                player.sendMessage("§cUtilize /vault remover {jogador} {nome}");
                            }
                            break;
                        case "listar":
                            if(args.length == 2) {
                                Player target = Bukkit.getPlayer(args[1]);
                                if(target != null) {
                                    vaultManager.listChests(target, player);
                                } else {
                                    player.sendMessage("§cEsse jogador não existe ou não está online.");
                                }
                            } else {
                                player.sendMessage("§cUtilize /vault listar {jogador}");
                            }
                            break;
                        case "abrir": // baus abrir {player} {name}
                            if(args.length == 3) {
                                Player target = Bukkit.getPlayer(args[1]);
                                if(target != null) {
                                    vaultManager.openPlayerChest(target, player, args[2]);
                                } else {
                                    player.sendMessage("§cEsse jogador não existe ou não está online.");
                                }
                            } else {
                                player.sendMessage("§cUtilize /vault comprar");
                            }
                            break;
                        default:
                            player.sendMessage("");
                            player.sendMessage(ChatColor.of("#4498DB") + "  §lVAULTS:");
                            player.sendMessage("");
                            player.sendMessage(" §8➟ §7/vault listar {jogador} §8- §fVeja os baus que o jogador tem.");
                            player.sendMessage(" §8➟ §7/vault open {jogador} {nome} §8- §fAbra um bau de um jogador.");
                            player.sendMessage(" §8➟ §7/vault dar {player} {tamanho} {nome}  §8- §fAdicione um bau para um jogador.");
                            player.sendMessage(" §8➟ §7/vault remover {player} {nome} §8- §fRemova um bau de um jogador.");
                            player.sendMessage("");

                            break;

                    }
                }
            }

        }

    }



}
