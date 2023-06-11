package Table;

import java.awt.*;
import javax.swing.*;
import java.util.HashMap;

public class Sheet extends JPanel {

    HashMap<CellIdentifier, Cell> cells = new HashMap<CellIdentifier, Cell>();

    public Sheet() {

    }

    public static void main(String[] args) {
        // Sheet s = new Sheet();

        // Frame f = new Frame();
        // f.setSize(1024, 1024);
        // f.add(s);
        // f.setVisible(true);
        // s.setVisible(true);

        String columnString = "ZBDHH";
        int columnInt = CellIdentifier.getColumnNumber(columnString);
        System.out.println(columnString);
        System.out.println(columnInt);
        System.out.println(CellIdentifier.getColumnString(columnInt));

    }
}