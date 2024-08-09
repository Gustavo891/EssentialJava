package org.essential.essentialBoss.Commands;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.essential.essentialBoss.Boss.Listener.BossUtils;
import org.essential.essentialBoss.Boss.Manager.BossConfig;
import org.essential.essentialBoss.Boss.Manager.BossSettings;
import org.essential.essentialBoss.EssentialBoss;

public class BossCommand implements CommandExecutor {
    private final EssentialBoss plugin;
    private final BossConfig bossConfig;

    private final NamespacedKey BOSS_TYPE = new NamespacedKey("essential_boss", "boss_type");
    private final NamespacedKey BOSS_QUANTITY = new NamespacedKey("essential_boss", "boss_quantity");
    private final BossUtils bossUtils;

    /*

    /boss - abre menu com informações
    /boss give {player} [tipo] [quantidade]

     */

    public BossCommand(EssentialBoss plugin, BossConfig bossConfig, BossUtils bossUtils) {
        this.plugin = plugin;
        this.bossConfig = bossConfig;
        this.bossUtils = bossUtils;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player) {
            if (args.length > 0) {
                switch (args[0]) {
                    case "give":
                        if (args.length == 4) {
                            Player target = Bukkit.getPlayer(args[1]);
                            if (target != null && target.isOnline()) {
                                try {
                                    BossSettings boss = bossConfig.getBossSettings(args[2]);
                                    int quantity = Integer.parseInt(args[3]);
                                    ItemStack egg = boss.createBossEgg(quantity);
                                    target.getInventory().addItem(egg); // futuramente enviar ao correio
                                    player.sendMessage(bossUtils.giveEgg(quantity, boss.getName(), target));
                                } catch (IllegalArgumentException e) {
                                    player.sendMessage(bossUtils.invalidoBoss());
                                }
                            } else {
                                player.sendMessage(bossUtils.invalidPlayer());
                            }
                        } else if (args.length == 3) {
                            try {
                                BossSettings boss = bossConfig.getBossSettings(args[1]);
                                int quantity = Integer.parseInt(args[2]);
                                ItemStack egg = boss.createBossEgg(quantity);
                                player.getInventory().addItem(egg); // futuramente enviar ao correio
                                player.sendMessage(bossUtils.getEgg(quantity, boss.getName()));
                            } catch (IllegalArgumentException e) {
                                player.sendMessage(bossUtils.invalidoBoss());
                            }
                        } else {
                            player.sendMessage(bossUtils.incorrectGiveEgg());
                        }
                        break;
                }
            }

        }
        return true;
    }

}
