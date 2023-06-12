package Table;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Table extends Frame implements KeyListener {

    private ArrayList<Sheet> sheets;
    private int currentSheetIndex;
    Sheet currentSheet;

    private InputField inputField;

    public Table() {
        setLayout(new BorderLayout());

        sheets = new ArrayList<Sheet>();
        sheets.add(new Sheet());
        currentSheetIndex = 0;
        currentSheet = sheets.get(currentSheetIndex);
        currentSheet.addKeyListener(this);
        add(currentSheet, BorderLayout.CENTER);

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
