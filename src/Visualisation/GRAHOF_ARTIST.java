package Visualisation;

import GRAHOF.*;

import javax.swing.*;

public class GRAHOF_ARTIST {

    private GRAHOF_CANVAS drawing;

    public GRAHOF_ARTIST(GRAHOF grahof) {
        JFrame frame = new JFrame("GRAHOF");
        frame.setSize(1000, 1000);
        drawing = new GRAHOF_CANVAS(grahof);
        frame.add(drawing);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public GRAHOF_LISTENER get_listener() {
        return this.drawing;
    }

}



