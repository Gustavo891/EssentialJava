package org.essential.essentialBoss.Sword.Listener;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

public class DropEvent implements Listener {

    private final NamespacedKey MATADORA = new NamespacedKey("essential_boss", "matadora");

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        if(data.has(MATADORA)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVocê não pode dropar a matadora de boss.");
        }

    }
}
