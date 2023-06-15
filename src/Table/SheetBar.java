package Table;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class SheetBar extends Panel implements MouseListener {

    ArrayList<Label> labels;
    MouseAdapter mouseAdapter;
    Label currentLabel = null;

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

    public String get(int index) {
        return labels.get(index).getText();
    }

    public void addSheet(String text) {
        Label newSheet = new Label(text, Label.CENTER);
        newSheet.setBackground(Color.LIGHT_GRAY);
        labels.add(labels.size() - 1, newSheet);
        newSheet.addMouseListener(mouseAdapter);
        add(newSheet, getComponentCount() - 1);
        newSheet.addMouseListener(this);
        if (currentLabel != null)
            currentLabel.setBackground(Color.LIGHT_GRAY);
        currentLabel = newSheet;
        currentLabel.setBackground(Color.GRAY);

    }

    public int getIndex(Label label) {
        int index = labels.indexOf(label);
        if (index == labels.size() - 1)
            return -1;
        return index;
    }

    public int getIndex(String sheetName) {
        for (int i = 0; i < labels.size() - 1; i++) {
            if (labels.get(i).getText().equals(sheetName)) {
                return i;
            }
        }
        return -1;
    }

    public void changeSheet(int index) {
        currentLabel.setBackground(Color.LIGHT_GRAY);
        currentLabel = labels.get(index);
        currentLabel.setBackground(Color.GRAY);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof Label) {
            currentLabel.setBackground(Color.LIGHT_GRAY);
            currentLabel = (Label) e.getSource();
            currentLabel.setBackground(Color.GRAY);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

}
