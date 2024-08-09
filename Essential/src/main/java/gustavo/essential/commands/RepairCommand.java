package gustavo.essential.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class RepairCommand extends Command{

    public RepairCommand() {
        super(
                "reparar",
                "Trocar o modo de jogo",
                "§cUse /gamemode (modo).",
                new String[]{"repair", "fix"},
                "essential.gamemode"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            return;
        } else {
            ItemStack item = player.getInventory().getItemInMainHand();
            if(item != null && item.getType() != Material.AIR) {
                Damageable damageable = (Damageable) item.getItemMeta();
                assert damageable != null;
                if(damageable.getDamage() > 0) {
                    damageable.setDamage(0);
                    item.setItemMeta(damageable);
                    player.sendMessage("§aSeu item foi reparado. Bom uso!");
                } else {
                    player.sendMessage("§cEsse item não pode ser reparado.");
                }
            } else {
                player.sendMessage("§cEsse não é um item válido.");
            }
        }

    }
}
