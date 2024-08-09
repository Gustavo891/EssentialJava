package gustavo.essentialmines.commands;

import gustavo.essentialmines.manager.FileManager;
import gustavo.essentialmines.Mine.MineManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SetupCommand extends Command {

    FileManager fileManager;
    MineManager mineManager;

    public SetupCommand(FileManager fileManager, MineManager mineManager) {
        super(
                "minasetup",
                "Faça a configuração de sua mina",
                "§cUse /setup ajuda.",
                new String[]{},
                "essential.minesetup"
        );
        this.fileManager = fileManager;
        this.mineManager = mineManager;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cUse /setup ajuda para mais informações.");
            return true;
        }

        String subCommand = args[0];

        FileConfiguration minesConfig = fileManager.getFile();

        switch (subCommand.toLowerCase()) {
            case "create":
                if(mineManager.getMine(args[1]) == null) {
                    mineManager.createMine(args[1], player.getLocation(), player.getLocation(), new HashMap<>(), player.getLocation());
                    player.sendMessage("§aA mina §7" + args[1] + "§a foi criada com sucesso.");
                    return true;
                }
                player.sendMessage("§cA mina §7" + args[1] + "§c já existe.");
                return true;
            case "delete":
                if(mineManager.getMine(args[1]) != null) {
                    mineManager.deleteMine(args[1]);
                    player.sendMessage("§aA mina §7" + args[1] + "§a foi deletada com sucesso.");
                    return true;
                }
                player.sendMessage("§cA mina §7" + args[1] + "§c não existe.");
                return true;
            case "edit":
                if(mineManager.getMine(args[1]) == null) {
                    player.sendMessage("§cA mina §7" + args[1] + "§c não existe.");
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage("§cUse /setup edit <mina> <pos1/pos2/ores> [chance]");
                    return true;
                }
                String editType = args[2];
                editMine(player, args[1], editType, args);
                return true;
            case "ajuda":
                player.sendMessage("");
                player.sendMessage(ChatColor.of("#4498DB") + "  §lMINE SETUP:");
                player.sendMessage("");
                player.sendMessage(" §8➟ §7/setup create {mina} §8- §fCriar a mina.");
                player.sendMessage(" §8➟ §7/setup edit <mina> <pos1/pos2/ores> [chance] §8- §fEditar a mina.");
                player.sendMessage(" §8➟ §7/setup save §8- §fSalvar as minas criadas.");
                player.sendMessage(" §8➟ §7/setup load §8- §fCarregar as minas da configuração.");
                player.sendMessage("");
                return true;
            case "save":
                mineManager.saveMines();
                player.sendMessage("§aMinas salvas.");
                return true;
            default:
                player.sendMessage("§cComando desconhecido. Use /setup ajuda para ajuda.");
                return true;
        }
    }

    private void editMine(Player player, String mineName, String editType, String[] args) {

        switch (editType.toLowerCase()) {
            case "pos1":
                mineManager.getMine(mineName).setPos1(player.getTargetBlockExact(5).getLocation());
                player.sendMessage("§aPos1 setada com sucesso.");
                break;

            case "pos2":
                mineManager.getMine(mineName).setPos2(player.getTargetBlockExact(5).getLocation());
                player.sendMessage("§aPos2 setada com sucesso.");
                break;

            case "ores":
                if (args.length != 4) {
                    player.sendMessage("§cUso: /setup edit <mina> ores <chance>");
                    return;
                }
                editOres(player, mineName, mineManager.getMine(mineName).getOres(), Integer.parseInt(args[3]));
                break;
            case "spawn":
                mineManager.getMine(mineName).setSpawn(player.getLocation());
                player.sendMessage("§aSpawn setado com sucesso.");
                break;
            default:
                player.sendMessage("§cTipo de edição desconhecido. Use /setup edit <mina> <pos1/pos2/ores> [chance]");
        }
    }

    private void editOres(Player player, String mineName, Map<String, Integer> ores, int chance) {

        String ore = player.getInventory().getItemInMainHand().getType().toString();
        if(ores.containsKey(ore)) {
            ores.replace(ore, chance);
            player.sendMessage("§aA porcentagem do minério §f" + ore + "§a foi alterada para §7" + chance + "%§a.");

        } else {
            ores.put(ore, chance);
            player.sendMessage("§aFoi adicionado o minério §f" + ore + "§a com a porcentagem §7" + chance + "%§a.");
        }

        mineManager.getMine(mineName).setOre(ores);

    }

}
