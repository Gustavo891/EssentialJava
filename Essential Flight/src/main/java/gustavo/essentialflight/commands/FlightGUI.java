package gustavo.essentialflight.commands;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import gustavo.essentialeconomy.Manager.CurrencyType;
import gustavo.essentialeconomy.Manager.EconomyManager;
import gustavo.essentialflight.EssentialFlight;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class FlightGUI{


    private final EconomyManager economyAPI;
    //private final LuckPerms luckPermsAPI;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final HashMap<UUID, Long> flightEndTimes = new HashMap<>();
    private final EssentialFlight plugin;
    private final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4"); // We reuse the same "random" UUID all the time

    public FlightGUI(EssentialFlight plugin) {
        this.plugin = plugin;
        this.economyAPI = plugin.getEconomyAPI();
        //this.luckPermsAPI = plugin.getLuckPermsAPI();
    }

    public void openMenu(Player player) {
        ChestGui baus = new ChestGui(4, "Voar:");

        StaticPane painel = new StaticPane(1,1,7,3);
        configPainel(painel, player);

        for(int i = 1; i <= 5; i++) {
            setLevel(painel, player, i);
        }

        baus.addPane(painel);
        baus.show(player);

    }

    private boolean timeLeft(UUID playerId) {
        if (cooldowns.containsKey(playerId) && System.currentTimeMillis() < cooldowns.get(playerId)) {
            long timeLeft = cooldowns.get(playerId) - System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    private boolean isCooldown(UUID playerId) {
        if (cooldowns.containsKey(playerId) && System.currentTimeMillis() < cooldowns.get(playerId)) {
            long timeLeft = cooldowns.get(playerId) - System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    private boolean isFly(UUID playerId) {
        return flightEndTimes.containsKey(playerId);
    }

    private void configPainel(StaticPane painel, Player player) {

        ItemStack sair = new ItemStack(Material.ARROW);
        ItemMeta sairMeta = sair.getItemMeta();
        sairMeta.setDisplayName("§cFechar");
        sair.setItemMeta(sairMeta);

        ItemStack flightActivate = new ItemStack(Material.FEATHER);
        ItemMeta flightMeta = flightActivate.getItemMeta();
        assert flightMeta != null;
        flightMeta.setDisplayName("§aModo de Voo");
        ArrayList<String> flightLore = new ArrayList<>();
        if(getPlayerLevel(player) == 0) {
            flightMeta.setDisplayName("§cModo de Voo");
            flightLore.add("§8Voo bloqueado, adquira ao lado.");
        } else {
            flightLore.add("§fNível: §7" + getPlayerLevel(player));
            flightLore.add("§r");
            if(isFly(player.getUniqueId())) {
                long timeLeft = flightEndTimes.get(player.getUniqueId()) - System.currentTimeMillis();
                flightLore.add("  §fTempo: §b" + getFlightDuration(getPlayerLevel(player)) + " minutos");
                flightLore.add("    " + getProgressBar(timeLeft, (long) getFlightDuration(getPlayerLevel(player)) * 60 * 1000));
                flightLore.add("§r");
                flightLore.add("§cVocê está com o voo ativado");
            } else if(isCooldown(player.getUniqueId())) {
                flightLore.add("  §fTempo: §b" + getFlightDuration(getPlayerLevel(player)) + " minutos");
                flightLore.add("    §2██████████§8 §7(100%)  ");
                flightLore.add("§r");
                long timeLeft = cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
                flightLore.add("§cAguarde " + (timeLeft / 1000 / 60) + " minutos e " + (timeLeft / 1000 % 60) + " segundos.");
            } else {
                flightLore.add("  §fTempo: §b" + getFlightDuration(getPlayerLevel(player)) + " minutos");
                flightLore.add("    §2██████████§8 §7(100%)  ");
                flightLore.add("§r");
                flightLore.add("§eClique para ativar o modo de voo.");
            }
        }
        flightMeta.setLore(flightLore);
        flightActivate.setItemMeta(flightMeta);

        GuiItem comprarGui = new GuiItem(flightActivate, event -> {
            event.setCancelled(true);
            if(getPlayerLevel(player) > 0) {
                if(!isFly(player.getUniqueId()) && !isCooldown(player.getUniqueId())) {
                    startFlight(player, getFlightDuration(getPlayerLevel(player)), getFlightDelay(getPlayerLevel(player)));
                }
                event.getWhoClicked().closeInventory();
            }
        });
        GuiItem sairGui = new GuiItem(sair, event -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        });

        painel.addItem(comprarGui, 0, 0);
        painel.addItem(sairGui, 3, 2);

    }

    private void setLevel(StaticPane painel, Player player, int level) {

        PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID);
        PlayerTextures textures = profile.getTextures();
        ItemStack level1 = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta level1Meta = (SkullMeta) level1.getItemMeta();
        URL urlObject;

        if(getPlayerLevel(player) >= level) {
            try {
                urlObject = new URL("https://textures.minecraft.net/texture/58e9325dd19289a11401ad844950bc319c3e3cb0e5034d2a5ccfb2d5099ff6e1");
            } catch (MalformedURLException exception) {
                throw new RuntimeException("Invalid URL", exception);
            }
            textures.setSkin(urlObject);
        } else {
            try {
                urlObject = new URL("https://textures.minecraft.net/texture/7a50bbc5413035d487f3d6623a591930e842c56241f76738d8be1fea2dc2cd2f");
            } catch (MalformedURLException exception) {
                throw new RuntimeException("Invalid URL", exception);
            }
            textures.setSkin(urlObject);
        }

        assert level1Meta != null;
        level1Meta.setOwnerProfile(profile);
        level1Meta.setDisplayName("§6Nível " + level);
        ArrayList<String> level1Lore = new ArrayList<>();
        level1Lore.add("§r");
        level1Lore.add("  §8┃ §fDuração: §b" + getFlightDuration(level) + " minutos");
        level1Lore.add("  §8┃ §fCooldown: §7" + getFlightDelay(level) + " minutos");
        level1Lore.add("");
        level1Lore.add("  §fCusto: §6✪" + getLevelCost(level));
        level1Lore.add("");
        if(getPlayerLevel(player) >= level) {
            level1Lore.add("§8Você já possui esse nível.");
        } else if (getPlayerLevel(player) == level - 1) {
            if(economyAPI.getPlayerCurrency(player.getUniqueId(), CurrencyType.cash) >= getLevelCost(level)) {
                level1Lore.add("§eClique para evoluir para esse nível");
            } else {
                level1Lore.add("§cVocê não possui dinheiro suficiente.");
            }
        } else {
            level1Lore.add("§cEvolua os níveis anteriores para liberar.");
        }
        level1Meta.setLore(level1Lore);
        level1.setItemMeta(level1Meta);

        GuiItem itemLevel = new GuiItem(level1, event -> {
            if(getPlayerLevel(player) == level - 1) {
                if(economyAPI.getPlayerCurrency(player.getUniqueId(), CurrencyType.cash) >= getLevelCost(level)) {
                    Bukkit.getServer().dispatchCommand(
                            Bukkit.getServer().getConsoleSender(),
                            "lp user " + player.getDisplayName() + " permission set essential.flight." + level);
                    economyAPI.removeCurrency(player.getUniqueId(), CurrencyType.cash, getLevelCost(level));
                    player.sendMessage("§aVocê adquiriu o nivel §f" + level + "§a do modo de voo, parabéns!");
                    event.getWhoClicked().closeInventory();
                } else {
                    player.sendMessage("§cVocê não possui cash suficiente.");
                    event.getWhoClicked().closeInventory();
                }
            }
            event.setCancelled(true);
        });
        painel.addItem(itemLevel, level + 1, 0);

    }

    private void startFlight(Player player, int durationMinutes, int delayMinutes) {
        player.setAllowFlight(true);
        player.setFlying(true);
        UUID playerId = player.getUniqueId();
        long flightEndTime = System.currentTimeMillis() + ((long) durationMinutes * 60 * 1000);
        flightEndTimes.put(playerId, flightEndTime);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setAllowFlight(false);
                player.setFlying(false);
                flightEndTimes.remove(playerId);
                cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (delayMinutes * 60 * 1000));
                player.sendMessage("Seu tempo de voo acabou. Você precisa esperar " + delayMinutes + " minutos para voar novamente.");
            }
        }.runTaskLater(plugin, durationMinutes * 60 * 20L); // 1 tick = 1/20 segundos
    }

    private String getProgressBar(long timeLeft, long totalTime) {
        int totalBars = 10;
        double percent = (double) timeLeft / totalTime;
        int filledBars = (int) (totalBars * percent);
        int emptyBars = totalBars - filledBars;

        String formatted = String.format("%.1f", percent * 100);

        return "§2█".repeat(Math.max(0, filledBars)) +
                "§8█".repeat(Math.max(0, emptyBars)) + " §7(" + formatted + "%)";
    }

    private int getPlayerLevel(Player player) {
        if (player.hasPermission("essential.flight.5")) return 5;
        if (player.hasPermission("essential.flight.4")) return 4;
        if (player.hasPermission("essential.flight.3")) return 3;
        if (player.hasPermission("essential.flight.2")) return 2;
        if (player.hasPermission("essential.flight.1")) return 1;
        return 0;
    }

    private int getLevelCost(int level) {
        return switch (level) {
            case 1 -> 500;
            case 2 -> 1200;
            case 3 -> 2000;
            case 4 -> 3500;
            case 5 -> 5000;
            default -> 0;
        };
    }

    private int getFlightDuration(int level) {
        return switch (level) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            case 5 -> 5;
            default -> 0;
        };
    }

    private int getFlightDelay(int level) {
        return switch (level) {
            case 1 -> 5;
            case 2 -> 4;
            case 3 -> 3;
            case 4 -> 2;
            case 5 -> 1;
            default -> 0;
        };
    }




}
