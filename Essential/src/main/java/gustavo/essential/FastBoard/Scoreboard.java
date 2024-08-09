package gustavo.essential.FastBoard;

import gustavo.essential.Essential;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Scoreboard implements Listener {

    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private Essential plugin;

    public Scoreboard(Essential essential) {
        essential.getLogger().info("Scoreboard sendo carregada...");
        essential.getServer().getScheduler().runTaskTimer(essential, () -> {
            for (FastBoard board : boards.values()) {
                updateBoard(board);
            }
        }, 0, 20);
        this.plugin = essential;
    }

    public final Map<UUID, FastBoard> getBoards() {
        return boards;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        plugin.getLogger().info("Player joined: " + player.getName());

        FastBoard board = new FastBoard(player);
        board.updateTitle(ChatColor.RED + "FastBoard");

        this.boards.put(player.getUniqueId(), board);
        updateBoard(board);

        player.setPlayerListHeaderFooter(
                "§r\n"
                + ChatColor.of("#789AF3") + "§lRANKUP ESSENTIAL\n" + "§fUma nova era para o §7§nRankUP§f.\n§r",
                "§r\n" + "§7Loja: §floja.essential.com.br\n" + "      §7Discord: §fdiscord.gg/essential      \n§");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        plugin.getLogger().info("Player quit: " + player.getName());

        FastBoard board = this.boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }

    public void updateBoard(FastBoard board) {
        board.updateTitle(ChatColor.of("#789AF3") + "§lESSENTIAL");
        board.updateLines(
                "",
                "  §fNível: §a✫1",
                "  §2◑◑◑◑§8◑◑◑◑◑◑ §740%",
                "",
                "  §fClan: §7[TST]",
                "",
                PlaceholderAPI.setPlaceholders(board.getPlayer(), "  §fCoins: §2$§f%gustavo_money%   §r"),
                PlaceholderAPI.setPlaceholders(board.getPlayer(), "  §fCash: §6✪%gustavo_cash%"),
                "",
                "§7www.gustavo.com.br"
        );
    }


}
