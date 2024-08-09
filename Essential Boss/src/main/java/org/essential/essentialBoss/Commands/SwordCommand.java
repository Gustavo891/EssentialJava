package org.essential.essentialBoss.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.essential.essentialBoss.EssentialBoss;
import org.essential.essentialBoss.Sword.SwordItem;
import org.jetbrains.annotations.NotNull;

public class SwordCommand implements CommandExecutor {

    private final SwordItem swordItem;

    public SwordCommand(EssentialBoss essentialBoss, SwordItem swordItem) {
        this.swordItem = swordItem;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*
        matadora give
         */
        if(sender instanceof Player player) {
            ItemStack item = swordItem.giveSword();
            if(args.length == 1) {
                player.getInventory().addItem(item);
                player.sendMessage("§aVocê recebeu uma §lMatadora§r§a de boss.");
            } else if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if(target != null && target.isOnline()) {
                    target.getInventory().addItem(item);
                    player.sendMessage("§aVocê enviou uma §lMatadora§r§a para o jogador §f" + target.getDisplayName() + "§a.");
                }

            } else {
                player.sendMessage("§cUtilize: /matadora give {player}");
            }
        }

        return false;
    }
}
