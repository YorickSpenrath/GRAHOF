package Visualisation;

import java.awt.*;

class PAINT_TOOLS {

    private static int LINE_WIDTH = 5;

    private static void draw_vertical_rounded_line(Graphics g, int x, int y, int l) {
        int t = LINE_WIDTH;
        g.fillOval(x, y, t, t);
        g.fillRect(x, y + t / 2, t, l - t);
        g.fillOval(x, y + l - t, t, t);
    }

    private static void draw_horizontal_rounded_line(Graphics g, int x, int y, int l) {
        int t = LINE_WIDTH;
        g.fillOval(x, y, t, t);
        g.fillRect(x + t / 2, y, l - t, t);
        g.fillOval(x + l - t, y, t, t);
    }

//    private void draw_diagonal_rounded_line(Graphics g, int x, int y, int l, boolean rising) {
//        int s = LINE_WIDTH;
//        double x1 = x + (0.5 * Math.sqrt(2.0) * l - s);
//        double y1 = rising ? y + (0.5 * Math.sqrt(2.0) * l - s) : y - (0.5 * Math.sqrt(2.0) * l - s);
//        double fp = s / 2.0 + s / 4.0 * Math.sqrt(2.0);
//        double fm = s / 2.0 - s / 4.0 * Math.sqrt(2.0);
//
//        g.fillOval(x, y, s, s);
//        int[] xp = new int[]{(int) (x + fp), (int) (x1 + fp), (int) (x1 + fm), (int) (x + fm)};
//        int[] yp = new int[]{(int) (y + fm), (int) (y1 + fm), (int) (y1 + fp), (int) (y + fp)};
//        g.fillPolygon(xp, yp, 4);
//        g.fillOval((int) x1, (int) y1, s, s);
//    }

    public static void draw_rounded_rect(Graphics g, int x, int y, int l) {
        int t = LINE_WIDTH;
        draw_vertical_rounded_line(g, x, y, l);
        draw_vertical_rounded_line(g, x + l - t, y, l);
        draw_horizontal_rounded_line(g, x, y, l);
        draw_horizontal_rounded_line(g, x, y + l - t, l);
    }

    public static void fill_diamond(Graphics g, int x, int y, int l) {
        g.fillPolygon(new int[]{x, x + l / 2, x + l, x + l / 2}, new int[]{y + l / 2, y, y + l / 2, y + l}, 4);
    }

//    public static void draw_choice(Graphics g, int x, int y, String text) {
//        g.setColor(PATH_COLOR);
//        fill_diamond(g, x, y, XOR_SIZE);
//        g.drawString(text, x + XOR_SIZE * 3 / 4, y + XOR_SIZE * 1 / 4);
//        g.setColor(XOR_FILL);
//        fill_diamond(g, x + LINE_WIDTH, y + LINE_WIDTH, XOR_SIZE - LINE_WIDTH * 2);
//    }
}