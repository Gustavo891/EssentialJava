package gustavo.essentialmines.Mine;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import gustavo.essentialmines.listeners.Cuboid;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

public class Mine {

    private final Cuboid cuboid;
    private final String type;
    private Map<String, Integer> ores;
    Hologram holograma;
    private Location spawn;

    public Mine(Location pos1, Location pos2, String type, Map<String, Integer> ores, Location spawn) {
        this.cuboid = new Cuboid(pos1, pos2);
        this.type = type;
        this.ores = ores;
        this.spawn = spawn;
    }

    public void setupHolo() {

        List<String> holoList = Arrays.asList(
                ChatColor.of("#4498DB") + "§lMINA " + type.toUpperCase(),
                "",
                "§fProgresso:",
                barraProgresso()
        );
        Location loc = cuboid.getCenter();
        loc.setY(cuboid.getUpperY() + 4);
        Hologram hologram = DHAPI.createHologram("mina" + type, loc, holoList);
        DHAPI.insertHologramLine(hologram, 0, Material.valueOf(getHoloOre()));
    }

    public String getHoloOre() {
        for (Map.Entry<String, Integer> entry : ores.entrySet()) {
            return entry.getKey();
        }
        return null;
    }

    public String barraProgresso() {
        int barrasTotais = 10;

        double percentual = getProgress();
        percentual = Math.min(percentual, 100);

        int barrasPreenchidas = (int) (percentual / 100 * barrasTotais);
        int barrasVazias = barrasTotais - barrasPreenchidas;

        return "§2" + "⬛".repeat(Math.max(0, barrasPreenchidas)) +
                "§8" +
                "⬛".repeat(Math.max(0, barrasVazias)) +
                " §7" + (int) percentual + "%";
    }

    public Location getPos1() {
        return cuboid.getLowerNE();
    }
    public Location getSpawn() {
        return spawn;
    }
    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setPos1(Location pos1) {
        this.cuboid.setLowerNE(pos1);
    }

    public Location getPos2() {
        return cuboid.getUpperSW();
    }

    public void setPos2(Location pos2) {
        this.cuboid.setUpperSW(pos2);
    }

    public String getName() {
        return type;
    }

    public Map<String, Integer> getOres() {
        return ores;
    }

    public void setOre(Map<String, Integer> ores) {
        this.ores = ores;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public String getInfo() {
        return "Mina: " + type + "\nPos1: " + getPos1().toString() + "\nPos2: " + getPos2().toString();
    }

    public double getProgress() {

        int total = 0;
        int quebrados = 0;
        for(Block block : cuboid.getBlocks()) {
            if(block.getType() == Material.AIR) {
                quebrados ++;
            }
            total ++;
        }

        return ((double) quebrados /total)*100;
    }

    public void reset() {
        List<Material> materials = new ArrayList<>();
        List<Integer> probabilities = new ArrayList<>();
        int totalProbability = 0;

        for (Map.Entry<String, Integer> entry : ores.entrySet()) {
            materials.add(Material.valueOf(entry.getKey()));
            probabilities.add(entry.getValue());
            totalProbability += entry.getValue();
        }

        Random random = new Random();
        for (Block block : cuboid.getBlocks()) {
            int roll = random.nextInt(totalProbability);
            int cumulativeProbability = 0;

            for (int i = 0; i < materials.size(); i++) {
                cumulativeProbability += probabilities.get(i);
                if (roll < cumulativeProbability) {
                    block.setType(materials.get(i));
                    break;
                }
            }
        }
        holograma = DHAPI.getHologram("mina" + type);
        assert holograma != null;
        DHAPI.setHologramLine(holograma, 4, barraProgresso());
    }
}
