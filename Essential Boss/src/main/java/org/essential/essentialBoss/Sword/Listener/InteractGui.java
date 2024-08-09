package org.essential.essentialBoss.Sword.Listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.essential.essentialBoss.Sword.SwordItem;
import org.essential.essentialBoss.Sword.gui.SwordGUI;

public class InteractGui implements Listener {

    private final SwordItem swordItem;

    public InteractGui(SwordItem swordItem) {
        this.swordItem = swordItem;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(item == null || item.getType() == Material.AIR) {
            return;
        }

        if(swordItem.isSword(item)) {
            if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                SwordGUI swordGUI = new SwordGUI(player, item, swordItem);
                swordGUI.open();
            }
        }
    }

}
