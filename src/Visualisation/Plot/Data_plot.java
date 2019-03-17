package Visualisation.Plot;

import java.awt.*;
import java.util.ArrayList;

public class Data_plot extends Baseplot {
    private final ArrayList<Double> x_data;
    private final ArrayList<Double> y_data;

    public Data_plot(ArrayList<Double> x_data, ArrayList<Double> y_data) {
        this.x_data = x_data;
        this.y_data = y_data;
    }

    public void set_lims() {
        this.x0 = this.x0 != null ? this.x0 : this.x_data.stream().mapToDouble(d -> d).min().getAsDouble();
        this.xe = this.xe != null ? this.xe : this.x_data.stream().mapToDouble(d -> d).max().getAsDouble();
        this.y0 = this.y0 != null ? this.y0 : this.y_data.stream().mapToDouble(d -> d).min().getAsDouble();
        this.ye = this.ye != null ? this.ye : this.y_data.stream().mapToDouble(d -> d).max().getAsDouble();
    }

    public void show(Graphics g, int x_loc, int y_loc) {
        if (this.x_data.isEmpty()) {
            return;
        }
        super.show(g, x_loc, y_loc);

        if (this.x_data.isEmpty()) {
            return;
        }

        if (this.x_data.size() != this.y_data.size()) {
            throw new IllegalStateException("|X| != |Y|");
        }


        double x_fac = PLOTSIZE / (xe - x0);
        double y_fac = PLOTSIZE / (ye - y0);

        g.setColor(PLOT_GRAPH_COLOR);
        int xprev = (int) ((x_data.get(0) - x0) * x_fac + x_loc);
        int yprev = (int) (-(y_data.get(0) - y0) * y_fac + y_loc + PLOTSIZE);
        for (int i = 1; i < x_data.size(); i++) {
            int xnext = (int) ((x_data.get(i) - x0) * x_fac + x_loc);
            int ynext = (int) (-(y_data.get(i) - y0) * y_fac + y_loc + PLOTSIZE);
            g.drawLine(xprev, yprev, xnext, ynext);
            xprev = xnext;
            yprev = ynext;
        }

    }
}
