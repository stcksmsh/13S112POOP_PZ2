package Table;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Table extends Frame implements KeyListener {

    private ArrayList<Sheet> sheets;
    Sheet currentSheet;

    private InputField inputField;
    private SheetBar sheetBar;

    public Table() {
        setLayout(new BorderLayout());
        /// initialize sheet array
        sheets = new ArrayList<Sheet>();
        currentSheet = null;

        /// now add the input field used for sheet editing
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

        /// now add the sheet bar, used to change between existing and add new sheets
        sheetBar = new SheetBar(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof Label) {
                    int index = sheetBar.getIndex((Label) e.getSource());
                    if (index >= 0) {
                        changeSheet(index);
                    } else {
                        String sheetName = JOptionPane.showInputDialog("Enter sheet name...");
                        System.err.println(sheetName);
                        addSheet(sheetName);
                    }
                } else {
                    System.err.println("ERROR, SOURCE IS NOT A LABEL");
                }
            }
        });
        add(sheetBar, BorderLayout.SOUTH);
        sheetBar.setVisible(true);

        /// now add the menu
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("File");

        MenuItem fileNew = new MenuItem("New", new MenuShortcut(KeyEvent.VK_N));
        file.add(fileNew);

        Menu fileOpen = new Menu("Open");
        file.add(fileOpen);
        MenuItem fileOpenCSV = new MenuItem("CSV", new MenuShortcut(KeyEvent.VK_O));
        fileOpen.add(fileOpenCSV);
        MenuItem fileOpenJSON = new MenuItem("JSON", new MenuShortcut(KeyEvent.VK_O, true));
        fileOpen.add(fileOpenJSON);

        Menu fileSave = new Menu("Save");
        file.add(fileSave);
        MenuItem fileSaveCSV = new MenuItem("CSV", new MenuShortcut(KeyEvent.VK_S));
        fileSave.add(fileSaveCSV);
        MenuItem fileSaveJSON = new MenuItem("JSON", new MenuShortcut(KeyEvent.VK_S, true));
        fileSave.add(fileSaveJSON);

        menuBar.add(file);
        setMenuBar(menuBar);
    }

    private void updateInputField() {
        inputField.setValues(currentSheet.getFocusedCell());
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
                inputField.requestFocus();
                updateInputField();
                break;
            case KeyEvent.VK_ESCAPE:
                break;
            default:
                currentSheet.keyPress(e);
                updateInputField();
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
    }
}
