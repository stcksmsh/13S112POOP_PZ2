package Table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.KeyListener;

public class InputField extends Panel {

    private Label cellID;
    private TextField value;
    private Label format;

    public InputField() {
        /// initially set to long empty string, to make the label wide
        cellID = new Label("          ");
        value = new TextField();
        /// same as cellID
        format = new Label("                        ");
        setLayout(new BorderLayout(10, 10));

        cellID.setBackground(Color.GRAY);
        format.setBackground(Color.GRAY);

        add(cellID, BorderLayout.LINE_START);
        add(value, BorderLayout.CENTER);
        add(format, BorderLayout.LINE_END);

    }

    @Override
    public synchronized void addKeyListener(KeyListener l) {
        value.addKeyListener(l);
    }

    public String getText() {
        return value.getText();
    }

    @Override
    public void requestFocus() {
        value.requestFocus();
    }

    public void setValues(Cell cell) {
        StringBuilder sb = new StringBuilder(cell.getCellIdentifier().getColumn());
        sb.append(cell.getCellIdentifier().getRow());
        cellID.setText(String.format("  %-7s", sb.toString()));
        value.setText(cell.getText());
        switch()
        this.format.setText(String.format("%14s  ", "TextFormat"));
    }
}
