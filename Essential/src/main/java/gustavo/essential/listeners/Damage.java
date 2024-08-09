package gustavo.essential.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class Damage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player player) {
            if(player.getWorld().getName().equals("mundo")) {
                e.setCancelled(true);
            }
        }
    }

}
