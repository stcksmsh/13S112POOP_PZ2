package Table;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.border.Border;

public class Table extends Frame implements KeyListener {

    private ArrayList<Sheet> sheets;
    Sheet currentSheet;

    private InputField inputField;
    private SheetBar sheetBar;

    public Table() {
        setLayout(new BorderLayout());

        sheets = new ArrayList<Sheet>();
        sheets.add(new Sheet());
        currentSheet = null;

        inputField = new InputField();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    currentSheet.setText(inputField.getText());
                    currentSheet.requestFocus();
                }
            }
        });
        add(inputField, BorderLayout.NORTH);
        sheetBar = new SheetBar(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof Label) {
                    int index = sheetBar.getIndex((Label) e.getSource());
                    if (index >= 0) {
                        changeSheet(index);
                    } else {
                        addSheet("TEST");
                    }
                } else {
                    System.err.println("ERROR, SOURCE IS NOT A LABEL");
                }
            }
        });

        add(sheetBar, BorderLayout.SOUTH);
        sheetBar.setVisible(true);
    }

    private void changeSheet(int index) {
        if (currentSheet != null)
            remove(currentSheet);
        currentSheet = sheets.get(index);
        add(currentSheet, BorderLayout.CENTER);
        currentSheet.addKeyListener(this);
        revalidate();
        currentSheet.init();
    }

    private void addSheet(String text) {
        if (currentSheet != null) {
            remove(currentSheet);
        }
        sheetBar.addSheet(text);
        currentSheet = new Sheet();
        sheets.add(currentSheet);
        currentSheet.addKeyListener(this);
        add(currentSheet, BorderLayout.CENTER);
        currentSheet.requestFocus();
        revalidate();
        currentSheet.init();

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case KeyEvent.VK_ENTER:
                inputField.setValues(currentSheet.getFocusedCell());
                inputField.requestFocus();
                break;
            case KeyEvent.VK_ESCAPE:
                break;
            default:
                currentSheet.keyPress(e);
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        Table t = new Table();
        t.setSize(500, 500);
        t.setVisible(true);
        t.currentSheet.init();
    }
}
