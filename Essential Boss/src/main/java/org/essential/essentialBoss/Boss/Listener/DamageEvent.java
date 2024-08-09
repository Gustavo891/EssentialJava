package org.essential.essentialBoss.Boss.Listener;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.essential.essentialBoss.Boss.Manager.BossConfig;
import org.essential.essentialBoss.Boss.Manager.BossSettings;
import org.essential.essentialBoss.Boss.Reward.Reward;
import org.essential.essentialBoss.EssentialBoss;
import org.essential.essentialBoss.Sword.SwordItem;

import java.util.List;
import java.util.Objects;

public class DamageEvent implements Listener {

    private final NamespacedKey BOSS_TYPE_KEY;
    private final NamespacedKey BOSS_STACK_KEY;
    private final NamespacedKey BOSS_HEALTH_KEY;
    private final EssentialBoss plugin;
    private final BossConfig bossConfig;
    private final BossUtils bossUtils;
    private final SwordItem swordItem;

    public DamageEvent(EssentialBoss plugin, BossConfig bossConfig, BossUtils bossUtils, SwordItem swordItem) {
        this.plugin = plugin;
        this.bossConfig = bossConfig;
        this.bossUtils = bossUtils;
        this.swordItem = swordItem;
        this.BOSS_TYPE_KEY = new NamespacedKey(plugin, "boss_type");
        this.BOSS_STACK_KEY = new NamespacedKey(plugin, "boss_stack");
        this.BOSS_HEALTH_KEY = new NamespacedKey(plugin, "boss_health");
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return; // Early return para entidades não-vivas
        }

        PersistentDataContainer entityData = entity.getPersistentDataContainer();
        if (!entityData.has(BOSS_TYPE_KEY, PersistentDataType.STRING)) {
            event.setCancelled(true);
            return;
        }

        if (!(event instanceof EntityDamageByEntityEvent damageEvent)) {
            event.setCancelled(true);
            return;
        }

        Entity damager = damageEvent.getDamager();
        if (!(damager instanceof Player player)) {
            event.setCancelled(true);
            return;
        }

        handleBossDamage(event, entity, entityData, player);
    }

    private void handleBossDamage(EntityDamageEvent event, LivingEntity entity, PersistentDataContainer entityData, Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!swordItem.isSword(item)) {
            event.setCancelled(true);
            player.sendMessage(bossUtils.matadora());
            return;
        }

        int dano = swordItem.getDano(item);
        if (dano < 0) {
            event.setCancelled(true);
            return;
        }

        BossSettings bossSettings = bossConfig.getBossSettings(entityData.get(BOSS_TYPE_KEY, PersistentDataType.STRING));
        int currentHealth = entityData.get(BOSS_HEALTH_KEY, PersistentDataType.INTEGER);
        int newHealth = currentHealth - dano;

        player.playSound(entity.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.1F, 1.0F);
        bossUtils.lifeMessage(currentHealth, bossConfig.barraProgresso(currentHealth, bossSettings.getHealth()), dano, player);

        if (newHealth <= 0) {
            swordItem.updateStatsKills(item);
            handleBossDeath(entity, entityData, player, bossSettings);
        } else {
            entityData.set(BOSS_HEALTH_KEY, PersistentDataType.INTEGER, newHealth);
        }

        event.setDamage(0);
    }

    private void handleBossDeath(LivingEntity entity, PersistentDataContainer entityData, Player player, BossSettings bossSettings) {
        bossUtils.killMessage(bossSettings.getName(), player);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        entity.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, entity.getLocation(), 10, 0.1, 0.1, 0.1, 0.02);

        List<Reward> rewards = bossSettings.getRewards();
        for (Reward reward : rewards) {
            player.getInventory().addItem(new ItemStack(reward.getItemStack(), reward.getQuantity()));
        }

        int stackCount = entityData.get(BOSS_STACK_KEY, PersistentDataType.INTEGER);
        if (stackCount > 1) {
            entityData.set(BOSS_STACK_KEY, PersistentDataType.INTEGER, stackCount - 1);
            entityData.set(BOSS_HEALTH_KEY, PersistentDataType.INTEGER, bossSettings.getHealth());
            entity.setCustomName("§e§lx" + (stackCount - 1) + "§r§e " + bossSettings.getName());
        } else {
            entity.remove();
        }
    }
}
