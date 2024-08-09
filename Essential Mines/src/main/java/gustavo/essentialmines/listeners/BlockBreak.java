package gustavo.essentialmines.listeners;

import gustavo.essentialeconomy.Manager.CurrencyType;
import gustavo.essentialeconomy.Manager.EconomyManager;
import gustavo.essentialmines.Mine.Mine;
import gustavo.essentialmines.Mine.MineManager;
import gustavo.essentialmines.Mine.Ores;
import gustavo.essentialmines.Mine.OresManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlockBreak implements Listener {

    private final MineManager mineManager;
    private final OresManager oresManager;
    private final EconomyManager economyAPI;

    public BlockBreak(MineManager mineManager, OresManager oresManager, EconomyManager economyAPI) {
        this.mineManager = mineManager;
        this.oresManager = oresManager;
        this.economyAPI = economyAPI;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Map<String, Mine> mines = mineManager.getMines();

            for (Map.Entry<String, Mine> entry : mines.entrySet()) {
                Cuboid cuboid = entry.getValue().getCuboid();
                if(cuboid.contains(e.getBlock().getLocation())) {
                    e.setDropItems(false);
                    sellOre(e.getBlock(), player, entry.getValue());
                } else if(!player.hasPermission("essential.bypass")) {
                    e.setCancelled(true);
                    player.sendMessage("§CVocê não pode quebrar bloco aqui.");
                }
            }


    }

    public void sellOre(Block block, Player player, Mine mine) {
        List<Ores> ores = oresManager.getOres();

        Optional<Ores> minerio = ores.stream()
                .filter(ore -> ore.getType() == block.getType())
                .findFirst();

        if(minerio.isPresent()) {

            TextComponent message = new TextComponent();
            message.setText(mine.getName().toUpperCase() + "§r§8 ➟ §2+ $§f" + minerio.get().getPrice());
            message.setColor(ChatColor.of("#789AF3"));
            message.setBold(true);

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
            economyAPI.addCurrency(player.getUniqueId(), CurrencyType.money, minerio.get().getPrice());

        }

    }

}
