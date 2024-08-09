package org.essential.essentialBoss;

import gustavo.essentialeconomy.EssentialEconomy;
import gustavo.essentialeconomy.Manager.EconomyManager;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.essential.essentialBoss.Commands.BossCommand;
import org.essential.essentialBoss.Boss.Listener.BossListener;
import org.essential.essentialBoss.Boss.Listener.BossUtils;
import org.essential.essentialBoss.Boss.Listener.DamageEvent;
import org.essential.essentialBoss.Boss.Manager.BossConfig;
import org.essential.essentialBoss.Commands.SwordCommand;
import org.essential.essentialBoss.Sword.Listener.DropEvent;
import org.essential.essentialBoss.Sword.Listener.InteractGui;
import org.essential.essentialBoss.Sword.SwordItem;

public final class EssentialBoss extends JavaPlugin {

    private BossConfig bossConfig;
    private BossUtils bossUtils;
    private SwordItem swordItem;
    private EconomyManager economyAPI;

    @Override
    public void onEnable() {

        EssentialEconomy essentialEconomy = (EssentialEconomy) getServer().getPluginManager().getPlugin("EssentialEconomy");
        assert essentialEconomy != null;
        economyAPI = essentialEconomy.getEconomyManager();
        saveDefaultConfig();
        bossConfig = new BossConfig(this);
        bossUtils = new BossUtils();
        swordItem = new SwordItem();

        getServer().getPluginManager().registerEvents(new BossListener(this, bossConfig, bossUtils), this);
        getServer().getPluginManager().registerEvents(new DamageEvent(this, bossConfig, bossUtils, swordItem), this);
        getServer().getPluginManager().registerEvents(new DropEvent(), this);
        getServer().getPluginManager().registerEvents(new InteractGui(swordItem), this);

        getCommand("boss").setExecutor(new BossCommand(this, bossConfig, bossUtils));
        getCommand("matadora").setExecutor(new SwordCommand(this, swordItem));
    }

    @Override
    public void onDisable() {
    }


    public static EssentialBoss getInstance() {
        return getPlugin(EssentialBoss.class);
    }
    public EconomyManager getEconomyAPI() { return economyAPI; }

    public BossConfig getBossConfig() {
        return bossConfig;
    }
}
