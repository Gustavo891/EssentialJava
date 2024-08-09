package org.essential.essentialBoss.Boss.Listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.essential.essentialBoss.Boss.Manager.BossSettings;

import java.util.ArrayList;
import java.util.List;

public class BossUtils {

    public void killMessage(String type, Player player) {
        TextComponent message = new TextComponent();
        message.setText("§fMatou! §eBoss§l " + type);
        message.setColor(ChatColor.of("#D45D67"));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
    }

    public void lifeMessage(int currentHealth, String progresso, int damage, Player player) {
        TextComponent message = new TextComponent();
        message.setText(currentHealth + "❤ " + progresso + " §7-" + (int) damage + "HP");
        message.setColor(ChatColor.of("#D45D67"));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
    }

    public String matadora() {
        return "§cVocê precisa utilizar uma matadora.";
    }

    public String plotAreaWarning() {
        return "§cVocê precisa estar em um terreno para spawnar o boss.";
    }

    public String unexpectedError() {
        return "§cOcorreu um erro inesperado. Contate um staff para lhe auxiliar.";
    }
    public String getEgg(int quantity, String bosstype) {
        return "§aVocê recebeu §7x" + quantity + " §a bosses do tipo §f" + bosstype + "§a.";
    }
    public String giveEgg(int quantity, String bosstype, Player target) {
        return "§aVocê enviou §7x" + quantity + " §a bosses do tipo §f" + bosstype + "§a para o jogador §f" + target.getDisplayName() + "§a.";
    }
    public String incorrectGiveEgg() {
        return "§cComando incorreto. Utilize /boss give {player} [tipo] [quantidade].";
    }
    public String invalidPlayer() {
        return "§cJogador não existe ou não está online.";
    }
    public String invalidoBoss() {
        return "§cA quantidade ou tipo de boss foi invalido.";
    }
    public String invalidPlace() {
        return "§cO boss pode ser apenas spawnado no chão.";
    }




}
