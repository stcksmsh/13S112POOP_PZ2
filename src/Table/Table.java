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
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JList;
import javax.swing.JOptionPane;

public class Table extends Frame
        implements KeyListener, ActionListener, MouseListener, Iterable<AbstractMap.SimpleEntry<String, Cell>> {

    private ArrayList<Sheet> sheets;
    Sheet currentSheet = null;

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
                    if (currentSheet != null) {
                        currentSheet.setText(inputField.getText());
                        currentSheet.requestFocus();
                    }
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

        Menu mnew = new Menu("New");
        {
            menuBar.add(mnew);
            MenuItem newFile = new MenuItem("New file", new MenuShortcut(KeyEvent.VK_N));
            mnew.add(newFile);
            newFile.addActionListener(this);
            MenuItem newSheet = new MenuItem("New sheet", new MenuShortcut(KeyEvent.VK_N, true));
            mnew.add(newSheet);
            newSheet.addActionListener(this);
        }

        Menu file = new Menu("File");
        {
            menuBar.add(file);

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

        setSize(500, 500);
        setVisible(true);

    }

    @Override
    public Iterator<AbstractMap.SimpleEntry<String, Cell>> iterator() {

        Iterator<AbstractMap.SimpleEntry<String, Cell>> it = new Iterator<AbstractMap.SimpleEntry<String, Cell>>() {
            private int sheetIndex = 0;
            Iterator<Cell> currentIterator = null;

            @Override
            public boolean hasNext() {
                if (currentIterator == null) /// only if next() has never been called
                    return sheets.size() > 0;
                if (sheetIndex > sheets.size())
                    return false;
                return sheetIndex < sheets.size() - 1 || currentIterator.hasNext();
            }

            @Override
            public AbstractMap.SimpleEntry<String, Cell> next() {
                if (currentIterator == null) {
                    currentIterator = sheets.get(sheetIndex).iterator();
                }
                if (!currentIterator.hasNext()) {
                    currentIterator = sheets.get(++sheetIndex).iterator();
                }
                return new AbstractMap.SimpleEntry<String, Cell>(sheetBar.labels.get(sheetIndex).getText(),
                        currentIterator.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("REMOVAL NOT ALLOWED");
            }
        };
        return it;
    }

    private void exit() {
        int input = JOptionPane.showConfirmDialog(this, "Your changes may be lost...", "Save document?",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (input == 0) {
            if (save() == 0)
                System.exit(0);
        }
        if (input == 1)
            System.exit(0);

    }

    private int save() {
        Object[] options = { "CSV",
                "JSON",
                "Cancel" };
        int n = JOptionPane.showOptionDialog(this, // parent container of JOptionPane
                "In what format would you like to save the file?",
                "Saving",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, // do not use a custom Icon
                options, // the titles of buttons
                options[0]);// default button title
        if (n == 0) {
            while (true) {
                CSVParser parser = new CSVParser();
                String filename = JOptionPane.showInputDialog(this, "Enter file name");
                if (filename == null)
                    break;
                if (parser.save(this, filename) != 0) {
                    JOptionPane.showMessageDialog(this, "Invalid filename '" + filename + "'...");
                } else {
                    return 0; /// wants to exit
                }
            }
        } else if (n == 1) {
            while (true) {
                JSONParser parser = new JSONParser();
                String filename = JOptionPane.showInputDialog(this, "Enter file name");
                if (filename == null)
                    break;
                if (parser.save(this, filename) != 0) {
                    JOptionPane.showMessageDialog(this, "Invalid filename '" + filename + "'...");
                } else {
                    return 0; /// wants to exit
                }
            }
        }
        return 1;
    }

    public void setValue(String sheetName, String column, int row, String value, String FormatCode) {
        int index = sheetBar.getIndex(sheetName);
        if (index == -1) {
            addSheet(sheetName);
            index = sheets.size() - 1;
        }
        sheets.get(index).setValue(column, row, value, FormatCode);
    }

    public void init() {
        currentSheet.init();
        updateInputField();
    }

    private void updateInputField() {
        inputField.setValues(currentSheet.getFocusedCell());
    }

    private void changeSheet(int index) {
        if (index < 0 || index >= sheets.size() || sheets.get(index) == currentSheet)
            return;
        if (currentSheet != null)
            remove(currentSheet);
        currentSheet = sheets.get(index);
        sheetBar.changeSheet(index);
        add(currentSheet, BorderLayout.CENTER);
        revalidate();
        currentSheet.init();
        updateInputField();
        currentSheet.requestFocus();
    }

    private void addSheet(String text) {
        if (currentSheet != null) {
            remove(currentSheet);
        }
        sheetBar.addSheet(text);
        currentSheet = new Sheet();
        sheets.add(currentSheet);
        currentSheet.addKeyListener(this);
        currentSheet.addMouseListener(this);
        add(currentSheet, BorderLayout.CENTER);
        currentSheet.requestFocus();
        revalidate();
        currentSheet.init();

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        updateInputField();
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
        CSVParser parser;
        String filename;
        switch (label) {
            case "New file":
                Table t = new Table();
                save();
                dispose();
                break;
            case "New sheet":
                String sheetName = JOptionPane.showInputDialog("Enter sheet name...");
                if (sheetName != null && !sheetName.equals("")) {
                    addSheet(sheetName);
                }
                break;
            case "Open from CSV":
                parser = new CSVParser();
                filename = JOptionPane.showInputDialog(this, "Enter file name");
                Table table = parser.open(filename);
                if (table == null) {
                    JOptionPane.showMessageDialog(this, "No such file '" + filename + "'!!!");
                } else {
                    dispose();
                }
                break;
            case "Open from JSON":
                break;
            case "Save to CSV":
                parser = new CSVParser();
                filename = JOptionPane.showInputDialog(this, "Enter file name");
                if (parser.save(this, filename) != 0) {
                    JOptionPane.showMessageDialog(this, "No such file '" + filename + "'!!!");
                }
                break;
            case "Save to JSON":
                break;
            case "NumberFormat":
                if (currentSheet == null)
                    return;
                try {
                    int precision = Integer.parseInt(JOptionPane.showInputDialog(this, "Precision for NumberFormat"));
                    currentSheet.setFormat(new NumberFormat(precision));
                    updateInputField();
                } catch (NumberFormatException nfe) {
                }
                break;
            case "DateFormat":
                currentSheet.setFormat(new DateFormat());
                updateInputField();
                break;
            case "TextFormat":
                currentSheet.setFormat(new TextFormat());
                updateInputField();
                break;
            default:
                System.err.print("Unknown action: ");
                System.err.println(e);
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                inputField.requestFocus();
                updateInputField();
                e.consume();
                break;
            case KeyEvent.VK_ESCAPE:
                e.consume();
                break;
            case KeyEvent.VK_LEFT:
                if (e.isAltDown()) {
                    if (currentSheet != null)
                        changeSheet(sheets.indexOf(currentSheet) - 1);
                } else {
                    currentSheet.keyPress(e);
                    updateInputField();
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (e.isAltDown()) {
                    if (currentSheet != null)
                        changeSheet(sheets.indexOf(currentSheet) + 1);
                } else {
                    currentSheet.keyPress(e);
                    updateInputField();
                }
                break;
            default:
                if (e.isAltDown() && (e.getKeyChar() >= '0' && e.getKeyChar() <= '9')) {
                    int index = e.getKeyChar() - '1';
                    if (index == -1) {
                        index = sheets.size() - 1;
                    }
                    index = Math.min(index, sheets.size() - 1);
                    changeSheet(index);
                } else {
                    currentSheet.keyPress(e);
                    updateInputField();
                }
                break;
        }
    }

    public Cell getCell(String sheetName, String cellID) {
        int index = sheetBar.getIndex(sheetName);
        CellIdentifier cellIdentifier = new CellIdentifier(cellID);
        Cell cell = sheets.get(index).getCell(cellIdentifier);
        return cell;
    }

    public CellValue getCellValueAndNotify(Formula source, String sheetName, String cellID) {
        int index = sheetBar.getIndex(sheetName);
        if (index == -1) {
            return null;
        }
        return sheets.get(index).getCellValueAndNotify(source, null, cellID);
    }

    public CellValue getCellValue(String sheetName, String cellID) {
        int index = sheetBar.getIndex(sheetName);
        if (index == -1) {
            return null;
        }
        return sheets.get(index).getCellValue(null, cellID);
    }

    public String getDisplayValue(String sheetName, String cellID) {
        int index = sheetBar.getIndex(sheetName);
        if (index == -1) {
            return null;
        }
        return sheets.get(index).getDisplayValue(null, cellID);
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

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        // Parser parser = new CSVParser();
        // Table t = parser.open("test.csv");
        Table t = new Table();
        // t.setTitle("test");
    }
}
