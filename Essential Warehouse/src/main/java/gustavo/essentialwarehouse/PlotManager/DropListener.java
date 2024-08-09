package gustavo.essentialwarehouse.PlotManager;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.PlotId;
import com.plotsquared.core.plot.PlotManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.Map;

public class DropListener implements Listener {

    private final Base base;

    public DropListener(Base base) {
        this.base = base;
    }

    @EventHandler
    public void onDropItem(ItemSpawnEvent event) {
        Item item = event.getEntity();
        ItemStack itemStack = item.getItemStack();
        Location loc = item.getLocation();
        com.plotsquared.core.location.Location plotLoc = BukkitUtil.adapt(loc);
        PlotArea plotArea = PlotSquared.get().getPlotAreaManager().getPlotArea(plotLoc);

        if (plotArea == null) {
            return;
        }

        Plot plot = plotArea.getPlot(plotLoc);
        if (plot == null || plot.getOwner() == null) {
            return;
        }
        if(base.checkId(plot.getId().toString())) {
            for(Map.Entry<Material, String> entry : base.getLista().entrySet()) {
                if(entry.getKey() == itemStack.getType()) {
                    event.setCancelled(true);
                    base.saveData(plot.getId().toString(), item.getItemStack().getType().toString(), item.getItemStack().getAmount());
                }
            }
        }

    }


}
