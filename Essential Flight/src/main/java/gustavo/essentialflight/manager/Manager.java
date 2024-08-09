package gustavo.essentialflight.manager;


import gustavo.essentialflight.EssentialFlight;
import gustavo.essentialflight.commands.FlightCommand;
import gustavo.essentialflight.commands.FlightGUI;

public class Manager {

    private final EssentialFlight plugin;
    FlightGUI flightGUI;

    public Manager(EssentialFlight plugin) {
        this.plugin = plugin;
    }

    public void register() {
        flightGUI = new FlightGUI(plugin);

        new FlightCommand(plugin, flightGUI);

        //plugin.getServer().getPluginManager().registerEvents(new onJoin(plugin), plugin);


    }
}
