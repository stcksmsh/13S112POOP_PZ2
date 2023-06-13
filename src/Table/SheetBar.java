package Table;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

public class SheetBar extends Panel {

    ArrayList<Label> labels;
    MouseAdapter mouseAdapter;

    public SheetBar(MouseAdapter mouseAdapter) {
        setLayout(new FlowLayout(FlowLayout.LEADING, 3, 5));
        labels = new ArrayList<Label>();
        this.mouseAdapter = mouseAdapter;
        Label newSheet = new Label("+", Label.CENTER);
        newSheet.setBackground(Color.LIGHT_GRAY);
        labels.add(newSheet);
        newSheet.addMouseListener(mouseAdapter);
        add(newSheet);

    }

    public void addSheet(String text) {
        Label newSheet = new Label(text, Label.CENTER);
        newSheet.setBackground(Color.LIGHT_GRAY);
        labels.add(labels.size() - 1, newSheet);
        newSheet.addMouseListener(mouseAdapter);
        add(newSheet, getComponentCount() - 1);
    }

    public int getIndex(Label label) {
        int index = labels.indexOf(label);
        if (index == labels.size() - 1)
            return -1;
        return index;
    }

}
