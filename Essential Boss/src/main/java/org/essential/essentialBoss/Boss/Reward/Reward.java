package org.essential.essentialBoss.Boss.Reward;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.essential.essentialBoss.Boss.Manager.BossSettings;

import java.util.List;

public class Reward {
    private final String name;
    private final Material itemStack;
    private final int quantity;
    private final List<String> commands;

    public Reward(String name, Material itemStack, int quantity, List<String> commands) {
        this.name = name;
        this.itemStack = itemStack;
        this.quantity = quantity;
        this.commands = commands;
    }

    public String getName() {
        return name;
    }

    public Material getItemStack() {
        return itemStack;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<String> getCommands() {
        return commands;
    }

}