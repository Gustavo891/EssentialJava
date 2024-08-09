package gustavo.essentialmines.Mine;

import org.bukkit.Material;

public class Ores {

    private Material type;
    private int price;

    public Ores(Material type, int price) {
        this.type = type;
        this.price = price;
    }

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
