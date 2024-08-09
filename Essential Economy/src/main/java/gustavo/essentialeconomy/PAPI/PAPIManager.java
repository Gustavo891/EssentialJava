package gustavo.essentialeconomy.PAPI;

import gustavo.essentialeconomy.Manager.CurrencyType;
import gustavo.essentialeconomy.Manager.EconomyManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIManager extends PlaceholderExpansion {

    EconomyManager economyManager;

    public PAPIManager(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    public static String formatNumber(double number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000);
        }
        else if (number >= 1_000) {
            return String.format("%.1fk", number / 1_000);
        }
        else {
            return String.valueOf((int) number);
        }
    }

    @Override
    public @NotNull String getIdentifier() {
        return "gustavo";
    }

    @Override
    public @NotNull String getAuthor() {
        return "gustavo891";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        return switch (params) {
            case "money" -> formatNumber(economyManager.getPlayerCurrency(player.getUniqueId(), CurrencyType.money));
            case "cash" -> formatNumber(economyManager.getPlayerCurrency(player.getUniqueId(), CurrencyType.cash));
            default -> null;
        };
    }

}