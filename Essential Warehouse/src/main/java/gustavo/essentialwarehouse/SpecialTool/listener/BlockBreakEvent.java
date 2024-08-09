package gustavo.essentialwarehouse.SpecialTool.listener;

import gustavo.essentialwarehouse.EssentialWarehouse;
import gustavo.essentialwarehouse.SpecialTool.ToolItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;


public class BlockBreakEvent implements Listener {

    ToolItem toolItem = EssentialWarehouse.getInstance().getToolItem();

    @EventHandler
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (toolItem.isSpecialHoe(item)) {
            if(toolItem.getBlocks().contains(event.getBlock().getType())) {
                Material blockType = event.getBlock().getType();
                    toolItem.addExperience(item, blockType, event.getPlayer());
            } else {
                event.setCancelled(true);
            }
        } else {
        }

    }

}
