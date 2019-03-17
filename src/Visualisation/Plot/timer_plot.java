package Visualisation.Plot;

import ConceptDrift.Drifter.FullLinearSumConceptDrifter;

import java.awt.*;


public class timer_plot extends Baseplot {

    private final FullLinearSumConceptDrifter cd_timer;

    public timer_plot(FullLinearSumConceptDrifter cd_timer) {
        this.cd_timer = cd_timer;
        this.set_xlim(0.0, 1.0 * cd_timer.get_L());
        this.set_ylim(0.0, 1.0 * cd_timer.get_L());
    }

    @Override
    void set_lims() {
    }

    public void show(Graphics g, int x_loc, int y_loc, double t) {
        super.show(g, x_loc, y_loc);
        double l = this.cd_timer.l(t);
        double h = this.cd_timer.h(t);

        double[] xpd, ypd;
        double L = this.cd_timer.get_L();
        if (l <= L && h > L) {
            xpd = new double[]{0, l, L, L, h - L, 0};
            ypd = new double[]{l, 0, 0, h - L, L, L};
        } else if (l <= L && h <= L) {
            xpd = new double[]{0, l, h, 0};
            ypd = new double[]{l, 0, 0, h};
        } else if (l > L && h > L) {
            xpd = new double[]{l - L, L, L, h - L};
            ypd = new double[]{L, l - L, h - L, L};
        } else {
            throw new IllegalArgumentException("l>h");
        }

        int[] xp = new int[xpd.length];
        int[] yp = new int[ypd.length];
        for (int i = 0; i < xpd.length; i++) {
            xp[i] = (int) (xpd[i] / L * PLOTSIZE + x_loc);
            yp[i] = (int) (-ypd[i] / L * PLOTSIZE + y_loc + PLOTSIZE);
        }

        g.setColor(PLOT_GRAPH_COLOR);
        g.fillPolygon(xp, yp, xpd.length);
    }
}
