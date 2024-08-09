package gustavo.essentialwarehouse.SpecialTool.listener;

import gustavo.essentialwarehouse.EssentialWarehouse;
import gustavo.essentialwarehouse.SpecialTool.ToolItem;
import gustavo.essentialwarehouse.SpecialTool.gui.ToolGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractEvent implements Listener {

    ToolItem tool = EssentialWarehouse.getInstance().getToolItem();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if(tool.isSpecialHoe(item)) {
                event.setCancelled(true);
                ToolGUI gui = new ToolGUI(item, event.getPlayer());
                gui.openMenu();
            }
        }
    }

}
