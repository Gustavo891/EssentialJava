package gustavo.essentialwarehouse.SpecialTool.commands;

import gustavo.essentialwarehouse.EssentialWarehouse;
import gustavo.essentialwarehouse.SpecialTool.ToolItem;
import gustavo.essentialwarehouse.SpecialTool.enchantments.EnchantmentList;
import gustavo.essentialwarehouse.Warehouse.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class giveTool extends Command {

    ToolItem tool = EssentialWarehouse.getInstance().getToolItem();

    public giveTool() {
        super(
                "givetool",
                "Comando para pegar a ferramenta especial.",
                "§cUse /givetool.",
                new String[]{"ferramenta"},
                ""
        );
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if(args.length == 0) {
                ItemStack specialAxe = tool.createSpecialHoe();
                player.getInventory().addItem(specialAxe);
                player.sendMessage("§aVocê recebeu uma enxada especial!");
                return true;
            } else if (args.length == 3) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if(tool.isSpecialHoe(item)) {
                    if(args[0].equalsIgnoreCase("enchantment")) {
                        switch(args[1].toLowerCase()) {
                            case "area":
                                tool.setEnchantment(EnchantmentList.AREA_BREAK, item, Integer.parseInt(args[2]), player);
                                break;
                            default:
                                player.sendMessage("§cEncantamento inexistente.");
                                break;
                        }
                    }
                }
            } else if (args.length == 2) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if(tool.isSpecialHoe(item)) {
                    if(args[0].equalsIgnoreCase("enchantment")) {
                        switch(args[1].toLowerCase()) {
                            case "area":
                                int level = tool.getEnchantment(EnchantmentList.AREA_BREAK, item);
                                player.sendMessage("§aO seu §7Quebra-área§a está no nível: §f" + level);
                                break;
                            default:
                                player.sendMessage("§cEncantamento inexistente.");
                                break;
                        }
                    }
                }
            }

        }
        return true;
    }

}

