package gustavo.essentialwarehouse.SpecialTool.enchantments;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotAPI;
import gustavo.essentialwarehouse.EssentialWarehouse;
import gustavo.essentialwarehouse.SpecialTool.ToolItem;
import gustavo.essentialwarehouse.SpecialTool.listener.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import java.util.Random;

public class AreaBreakEnchant implements Listener {

    private final ToolItem toolItem;

    public AreaBreakEnchant(ToolItem toolItem) {
        this.toolItem = toolItem;
    }

    @EventHandler
    public void onAreaBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        Material block = event.getBlock().getType();
        if (toolItem.isSpecialHoe(item)) {
            if(toolItem.getBlocks().contains(event.getBlock().getType())) {
                int level = toolItem.getEnchantment(EnchantmentList.AREA_BREAK, item);
                if(level > 0) {
                    int chance = new Random().nextInt(2000);
                    if(chance <= level) {
                        Location center = event.getBlock().getLocation();
                        Location corner1 = center.clone().add(-1, 0, -1);
                        Location corner2 = center.clone().add(1, 0, 1);
                        Cuboid area = new Cuboid(corner1, corner2);

                        for(Block quebrado : area.getBlocks()) {
                            if(quebrado.getType() == block) {
                                com.plotsquared.core.location.Location blockLock = BukkitUtil.adapt(quebrado.getLocation());
                                if(!blockLock.isPlotRoad()) {
                                    quebrado.breakNaturally(item);
                                    toolItem.addExperience(item, block, player);
                                    quebrado.getWorld().spawnParticle(Particle.FLAME, quebrado.getLocation(), 10, 0.1, 0.1, 0.1, 0.02);
                                }

                            }
                        }
                    }
                } else {}

            }

        }

    }

}
