package org.essential.essentialBoss.Boss.Manager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.essential.essentialBoss.Boss.Reward.Reward;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BossSettings {
    private final String name;
    private final int health;
    private final EntityType entityType;
    private final String head;
    private final List<Reward> rewards;
    private final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4"); // We reuse the same "random" UUID all the time

    private final NamespacedKey BOSS_TYPE = new NamespacedKey("essential_boss", "boss_type");
    private final NamespacedKey BOSS_QUANTITY = new NamespacedKey("essential_boss", "boss_quantity");

    public BossSettings(String name, int health, EntityType entityType, List<Reward> rewards, String head) {
        this.name = name;
        this.health = health;
        this.entityType = entityType;
        this.rewards = rewards;
        this.head = head;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public String getHead() {
        return head;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public ItemStack createBossEgg(int quantity) {

        ItemStack egg = new ItemStack(Material.PLAYER_HEAD);
        PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID);
        PlayerTextures textures = profile.getTextures();
        SkullMeta eggMeta = (SkullMeta) egg.getItemMeta();
        try {
            URL urlObject = new URL("https://textures.minecraft.net/texture/" + head);
            textures.setSkin(urlObject);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        assert eggMeta != null;
        eggMeta.setOwnerProfile(profile);
        PersistentDataContainer data = eggMeta.getPersistentDataContainer();

        data.set(BOSS_TYPE, PersistentDataType.STRING, getName());
        data.set(BOSS_QUANTITY, PersistentDataType.INTEGER, quantity);
        updateLore(eggMeta);
        egg.setItemMeta(eggMeta);

        return egg;
    }

    public void updateLore(SkullMeta meta) {
        int quantity = meta.getPersistentDataContainer().get(BOSS_QUANTITY, PersistentDataType.INTEGER);
        meta.setDisplayName("§e" + getName());
        List<String> lore = new ArrayList<>();
        lore.add("§7Quantidade: §f" + quantity);
        lore.add("");
        lore.add("§8Clique no chão para ativa-lo.");
        meta.setLore(lore);
    }

    public void decrementEggQuantity(Player player, ItemStack egg) {
        SkullMeta meta = (SkullMeta) egg.getItemMeta();
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();

            if (data.has(BOSS_TYPE, PersistentDataType.STRING)) {
                int quantity = data.get(BOSS_QUANTITY, PersistentDataType.INTEGER);
                if (quantity > 1) {
                    quantity--;
                    data.set(BOSS_QUANTITY, PersistentDataType.INTEGER, quantity);
                    updateLore(meta);
                    egg.setItemMeta(meta);
                } else {
                    player.getInventory().remove(egg);
                }
            }
        }
    }



}