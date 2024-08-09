package org.essential.essentialBoss.Boss.Listener;

import com.plotsquared.bukkit.util.BukkitUtil;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.essential.essentialBoss.Boss.Manager.BossConfig;
import org.essential.essentialBoss.Boss.Manager.BossSettings;
import org.essential.essentialBoss.EssentialBoss;

import java.util.Objects;

public class BossListener implements Listener {

    private final NamespacedKey BOSS_TYPE = new NamespacedKey("essential_boss", "boss_type");
    private final EssentialBoss plugin;
    private final BossConfig bossConfig;
    private final BossUtils bossUtils;
    private final NamespacedKey BOSS_TYPE_KEY;
    private final NamespacedKey BOSS_STACK_KEY;
    private final NamespacedKey BOSS_HEALTH_KEY;

    public BossListener(EssentialBoss plugin, BossConfig bossConfig, BossUtils bossUtils) {
        this.plugin = plugin;
        this.bossConfig = bossConfig;
        this.bossUtils = bossUtils;
        this.BOSS_TYPE_KEY = new NamespacedKey(plugin, "boss_type");
        this.BOSS_STACK_KEY = new NamespacedKey(plugin, "boss_stack");
        this.BOSS_HEALTH_KEY = new NamespacedKey(plugin, "boss_health");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) return;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;

        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        if (!data.has(BOSS_TYPE, PersistentDataType.STRING)) {
            return;
        }


        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            event.setCancelled(true);
            return;
        }

        Location blockLocation = event.getClickedBlock().getLocation();
        com.plotsquared.core.location.Location plotLoc = BukkitUtil.adapt(blockLocation);

        if (plotLoc == null || plotLoc.isPlotRoad() || !plotLoc.isPlotArea() || plotLoc.isUnownedPlotArea()) {
            player.sendMessage(bossUtils.plotAreaWarning());
            event.setCancelled(true);
            return;
        }

        String bossType = data.getOrDefault(BOSS_TYPE, PersistentDataType.STRING, "zumbi");
        BossSettings bossSettings = bossConfig.getBossSettings(bossType);

        try {
            spawnOrStackBoss(player, blockLocation, bossSettings, bossType, item);
            event.setCancelled(true);
        } catch (Exception e) {
            plugin.getLogger().severe("Error handling boss spawn: " + e.getMessage());
        }
    }

    private void spawnOrStackBoss(Player player, Location location, BossSettings bossSettings, String bossType, ItemStack item) {
        LivingEntity existingBoss = findNearbyBoss(player, location);

        if (existingBoss == null) {
            spawnNewBoss(player, location, bossSettings, bossType, item);
        } else {
            stackExistingBoss(existingBoss, bossSettings, item, player);
        }
    }

    private LivingEntity findNearbyBoss(Player player, Location location) {
        return player.getWorld().getNearbyEntities(location, 2, 2, 2).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(livingEntity -> livingEntity.getPersistentDataContainer().has(BOSS_TYPE_KEY, PersistentDataType.STRING))
                .findFirst()
                .orElse(null);
    }

    private void spawnNewBoss(Player player, Location location, BossSettings bossSettings, String bossType, ItemStack item) {
        Location spawnLocation = location.clone().add(0, 1, 0);
        LivingEntity boss = (LivingEntity) player.getWorld().spawnEntity(spawnLocation, bossSettings.getEntityType());

        boss.setCustomName("§e§lx1 §r§e" + bossSettings.getName());
        boss.setAI(false);
        boss.setGravity(false);

        PersistentDataContainer bossData = boss.getPersistentDataContainer();
        bossData.set(BOSS_TYPE_KEY, PersistentDataType.STRING, bossType);
        bossData.set(BOSS_STACK_KEY, PersistentDataType.INTEGER, 1);
        bossData.set(BOSS_HEALTH_KEY, PersistentDataType.INTEGER, bossSettings.getHealth());

        bossSettings.decrementEggQuantity(player, item);
    }

    private void stackExistingBoss(LivingEntity existingBoss, BossSettings bossSettings, ItemStack item, Player player) {
        PersistentDataContainer bossData = existingBoss.getPersistentDataContainer();

        int stackCount = bossData.getOrDefault(BOSS_STACK_KEY, PersistentDataType.INTEGER, 1) + 1;
        String bossName = bossSettings.getName();

        existingBoss.setCustomName("§e§lx" + stackCount + "§r§e " + bossName);
        bossData.set(BOSS_STACK_KEY, PersistentDataType.INTEGER, stackCount);

        bossSettings.decrementEggQuantity(player, item);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.getPersistentDataContainer().has(BOSS_TYPE_KEY, PersistentDataType.STRING)) {
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        if (event.getEntity().getPersistentDataContainer().has(BOSS_TYPE_KEY, PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }
}
