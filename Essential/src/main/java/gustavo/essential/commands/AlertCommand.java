package gustavo.essential.commands;

import gustavo.essential.manager.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class AlertCommand extends Command {

    private final FileManager configManager;

    public AlertCommand(FileManager configManager) {
        super(
                "alert",
                "Mande uma mensagem em destaque para todos jogadores.",
                "§cUse /alert (msg).",
                new String[]{"alerta"},
                "essential.alert"
        );
        this.configManager = configManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        FileConfiguration config = configManager.getFile();
        String prefix = config.getString("alert-prefix");

        if(args.length > 0 && prefix != null) {
            String formattedPrefix = ChatColor.translateAlternateColorCodes('&', prefix);
            String formattedText = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("  " + formattedPrefix + " §7" + formattedText);
            Bukkit.broadcastMessage("");
        } else {
            sender.sendMessage("§cUtilize /alerta (mensagem) para enviar uma mensagem para o servidor.");
        }

    }
}
