package gustavo.essentialwarehouse.PlotManager;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.events.PlotDeleteEvent;
import com.plotsquared.core.plot.Plot;

public class RemoveListener {

    private final Base base;

    public RemoveListener(Base base) {
        this.base = base;
    }

    @Subscribe
    public void onRemovePlot(PlotDeleteEvent event) {
        Plot plot = event.getPlot();
        String id = plot.getId().toString();
        base.deleteId(id);
    }

}
