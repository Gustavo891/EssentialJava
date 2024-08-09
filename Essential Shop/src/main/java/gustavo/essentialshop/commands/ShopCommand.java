package gustavo.essentialshop.commands;

import gustavo.essentialshop.EssentialShop;
import gustavo.essentialshop.Shop.ShopManager;
import gustavo.essentialshop.commands.gui.ShopInterface;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ShopCommand extends Command{

    private final EssentialShop plugin;
    private ShopManager shopManager;
    private List<String> categorias = Arrays.asList("blocos", "ferramentas", "plantacao", "utilitarios", "outros");

    public ShopCommand(EssentialShop plugin) {
        super(
                "shop",
                "Comando para abrir sua loja virtual",
                "§cUse /shop.",
                new String[]{"loja", "mercado"},
                ""
        );
        this.plugin = plugin;
        this.shopManager = plugin.getShopManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }
        if(args.length == 0) {
            ShopInterface shop = new ShopInterface(player, plugin);
            shop.openMenu();

        } else {
            if(player.hasPermission("essential.shop.admin")) { // shop add {categoria} {custo} {nome}
                    if(Objects.equals(args[0], "add")) {
                        if(args.length > 3) {
                            Material material = player.getInventory().getItemInMainHand().getType();
                            if(material != Material.AIR && material != null) {

                                if(categorias.contains(args[1].toLowerCase())) {
                                    int custo = Integer.parseInt(args[2]);
                                    if(custo > 0) {
                                        String nome = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                                        shopManager.addProduct(args[1], material, custo, nome, player);
                                    } else {
                                        player.sendMessage("§cO custo do produto deve ser maior que 0.");
                                    }
                                } else {
                                    player.sendMessage("§cEssa categoria não existe.");
                                }
                            } else {
                                player.sendMessage("§cVocê está segurando um item inválido.");
                            }
                        } else {
                            player.sendMessage("§cComando incorreto. Use /shop add {categoria} {custo} {nome}.");
                        }
                    } else if(Objects.equals(args[0], "save")) {
                        shopManager.saveCategories();
                        player.sendMessage("§aCategorias salvas com sucesso.");
                    }
            }
        }

        return true;
    }


}
