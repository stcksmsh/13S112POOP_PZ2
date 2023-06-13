package Table;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Table extends Frame implements KeyListener, ActionListener {

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
                        if (!sheetName.equals(""))
                            addSheet(sheetName);
                    }
                } else {
                    System.err.println("ERROR, SOURCE IS NOT A LABEL");
                }
            }
        });
        add(sheetBar, BorderLayout.SOUTH);
        sheetBar.setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                exit();
            };
        });

        /// now add the menu
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("File");
        {
            menuBar.add(file);

            {
                MenuItem fileNew = new MenuItem("New", new MenuShortcut(KeyEvent.VK_N));
                file.add(fileNew);
                fileNew.addActionListener(this);
            }

            {
                Menu fileOpen = new Menu("Open");
                file.add(fileOpen);
                MenuItem fileOpenCSV = new MenuItem("Open from CSV", new MenuShortcut(KeyEvent.VK_O));
                fileOpen.add(fileOpenCSV);
                fileOpenCSV.addActionListener(this);
                MenuItem fileOpenJSON = new MenuItem("Open from JSON", new MenuShortcut(KeyEvent.VK_O, true));
                fileOpen.add(fileOpenJSON);
                fileOpenJSON.addActionListener(this);
            }

            {
                Menu fileSave = new Menu("Save");
                file.add(fileSave);
                MenuItem fileSaveCSV = new MenuItem("Save to CSV", new MenuShortcut(KeyEvent.VK_S));
                fileSave.add(fileSaveCSV);
                fileSaveCSV.addActionListener(this);
                MenuItem fileSaveJSON = new MenuItem("Save to JSON", new MenuShortcut(KeyEvent.VK_S, true));
                fileSave.add(fileSaveJSON);
                fileSaveJSON.addActionListener(this);
            }
        }

        Menu format = new Menu("Format");
        {
            menuBar.add(format);

            {
                MenuItem formatNumber = new MenuItem("NumberFormat", new MenuShortcut(KeyEvent.VK_F));
                format.add(formatNumber);
                formatNumber.addActionListener(this);
            }

            {
                MenuItem formatDate = new MenuItem("DateFormat", new MenuShortcut(KeyEvent.VK_F, true));
                format.add(formatDate);
                formatDate.addActionListener(this);
            }

            {
                MenuItem formatText = new MenuItem("TextFormat");
                format.add(formatText);
                formatText.addActionListener(this);
            }
        }

        setMenuBar(menuBar);
    }

    private void exit() {
        int input = JOptionPane.showConfirmDialog(this, "Your changes may be lost...", "Save document?",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (input == 0)
            save();
        if (input == 1)
            System.exit(0);

    }

    private void save() {
        System.err.println("SAVING IS TO BE IMPLEMENTED");
        System.exit(0);
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
    public void actionPerformed(ActionEvent e) {/// only used on MenuItems
        if (!(e.getSource() instanceof MenuItem)) {
            System.err.println("SOURCE MUST BE OF TYPE 'MenuItem'");
            System.err.println(e);
            return;
        }
        MenuItem src = (MenuItem) e.getSource();
        String label = src.getLabel();
        switch (label) {
            case "New":
                break;
            case "Open from CSV":
                break;
            case "Open from JSON":
                break;
            case "Save to CSV":
                break;
            case "Save to JSON":
                break;
            case "NumberFormat":
                currentSheet.setFormat(new NumberFormat(3));
                break;
            case "DateFormat":
                currentSheet.setFormat(new DateFormat());
                break;
            case "TextFormat":
                currentSheet.setFormat(new TextFormat());
                break;
            default:
                System.err.print("Unknown action: ");
                System.err.println(e);
                break;
        }
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
