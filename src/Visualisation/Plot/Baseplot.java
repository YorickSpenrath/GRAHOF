package Visualisation.Plot;

import Visualisation.GRAHOF_CANVAS;

import java.awt.*;
import java.text.DecimalFormat;

public abstract class Baseplot {

    static final Color PLOT_GRAPH_COLOR = Color.BLUE;
    private static final Color PLOT_AXIS_COLOR = Color.BLACK;
    public static int PLOTSIZE = 100;
    private DecimalFormat df = new DecimalFormat("0.0");


    Double x0 = null;
    Double xe = null;
    Double y0 = null;
    Double ye = null;
    private String title = null;

    public final Baseplot setDF(DecimalFormat df) {
        this.df = df;
        return this;
    }

    final Baseplot set_xlim(double x0, double xe) {
        this.x0 = x0;
        this.xe = xe;
        return this;
    }

    public final Baseplot set_title(String s) {
        this.title = s;
        return this;
    }

    public final Baseplot set_ylim(double y0, double ye) {
        this.y0 = y0;
        this.ye = ye;
        return this;
    }

    abstract void set_lims();

    public void show(Graphics g, int x_loc, int y_loc) {
        this.set_lims();
        g.setColor(PLOT_AXIS_COLOR);
        g.drawLine(x_loc, y_loc, x_loc, y_loc + PLOTSIZE);
        g.drawLine(x_loc, y_loc + PLOTSIZE, x_loc + PLOTSIZE, y_loc + PLOTSIZE);

        g.setColor(GRAHOF_CANVAS.TEXTCOLOR);
        if (this.title != null) {
            g.drawString(this.title, x_loc + GRAHOF_CANVAS.VTS, y_loc);
        }

        g.drawString(df.format(x0), x_loc, y_loc + PLOTSIZE + GRAHOF_CANVAS.VTS);
        g.drawString(df.format(xe), x_loc + PLOTSIZE, y_loc + PLOTSIZE + GRAHOF_CANVAS.VTS);
        g.drawString(df.format(y0), x_loc - 2 * GRAHOF_CANVAS.VTS, y_loc + PLOTSIZE);
        g.drawString(df.format(ye), x_loc - 2 * GRAHOF_CANVAS.VTS, y_loc);
    }
}