package Table;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.*;
import javax.swing.SpringLayout.Constraints;

import java.util.HashMap;

public class Sheet extends JPanel implements MouseListener, MouseWheelListener, KeyListener {

    private static int border = 1; /// size of border between adjacent cells

    private HashMap<CellIdentifier, Cell> cells = new HashMap<CellIdentifier, Cell>(); /// the HashMap containing all of
                                                                                       /// the cells
    private Cell foucsedCell; /// the cell currently being focused
    private Cell topLeftCell; /// the cell in the top left corner of the sheet

    public Sheet() {
        super();
        createCell(1, 1);
        foucsedCell = cells.get(new CellIdentifier("A", 1));
        topLeftCell = foucsedCell;
        foucsedCell.setBackground(Color.GRAY);
        setBackground(Color.black);
        setLayout(new SpringLayout());
        setOpaque(true);
    }

    public void init() {
        makeSheet();
        changeFocus(foucsedCell);
    }

    private Cell createCell(int column, int row) {
        CellIdentifier id = new CellIdentifier(CellIdentifier.columnNumberToString(column), row);
        Cell cell = new Cell(id);
        cell.addMouseListener(this);
        cell.addMouseWheelListener(this);
        cells.put(id, cell);
        return cell;
    }

    private void makeSheet() { /// tunes the SpringLayout of the Sheet
        removeAll(); /// clears the grid from all of its components, they will be added again
        makeCompactGrid();
        revalidate();
        changeFocus(foucsedCell);
    }

    private void changeFocus(Cell cell) {
        foucsedCell.setBackground(Color.WHITE);
        foucsedCell = cell;
        foucsedCell.setBackground(Color.GRAY);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isControlDown()) {
            CellIdentifier topLeftId = topLeftCell.getCellIdentifier();
            int columnNumber = topLeftId.getColumnNumber();
            columnNumber++;
            String column = CellIdentifier.columnNumberToString(columnNumber);
            CellIdentifier newTopLeftId = new CellIdentifier(column, topLeftId.getRow());
            topLeftCell = cells.get(newTopLeftId);
            makeSheet();
            return;
        }
        Object obj = e.getSource();
        if (obj instanceof Cell)
            changeFocus((Cell) obj);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (notches < 0) { /// moved up
            CellIdentifier topLeftId = topLeftCell.getCellIdentifier();
            int row = topLeftId.getRow();
            if (row > 1) {
                row--;
                CellIdentifier newTopLeftId = new CellIdentifier(topLeftId.getColumn(), row);
                topLeftCell = cells.get(newTopLeftId);
                makeSheet();
            }
        } else {
            CellIdentifier topLeftId = topLeftCell.getCellIdentifier();
            int row = topLeftId.getRow();
            row++;
            CellIdentifier newTopLeftId = new CellIdentifier(topLeftId.getColumn(), row);
            topLeftCell = cells.get(newTopLeftId);
            makeSheet();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        CellIdentifier focusedCellIdentifier = foucsedCell.getCellIdentifier();
        int focusedColumn = CellIdentifier.columnStringToNumber(focusedCellIdentifier.getColumn());
        int focusedRow = focusedCellIdentifier.getRow();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (focusedRow > 1) {
                    focusedRow--;
                }
                break;
            case KeyEvent.VK_DOWN:
                focusedRow++;
                break;
            case KeyEvent.VK_LEFT:
                if (focusedColumn > 1) {
                    focusedColumn--;
                }
                break;
            case KeyEvent.VK_RIGHT:
                focusedColumn++;
                break;
        }
        String column = CellIdentifier.columnNumberToString(focusedColumn);
        changeFocus(cells.getOrDefault(new CellIdentifier(column, focusedRow), new Cell(column, focusedRow)));
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
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

    public void makeCompactGrid() {
        SpringLayout layout = (SpringLayout) this.getLayout();
        /// X and Y coordinate counters
        int x = 0;
        int y = 0;

        /// height and width of the container
        int height = getHeight();
        int width = getWidth();

        /// row and column of cell being looked at currently
        int column = topLeftCell.getCellIdentifier().getColumnNumber();
        int row = topLeftCell.getCellIdentifier().getRow();

        /// number of fully visible rows and columns in the grid
        int rows = 0;
        int columns = 0;

        int maxWidth = 0;

        do {
            y = 0;
            do {
                Cell cell = cells.getOrDefault(new CellIdentifier(column, row), createCell(column, row));
                row++;
                add(cell);
                Constraints constraints = layout.getConstraints(cell);
                constraints.setY(Spring.constant(y));
                constraints.setX(Spring.constant(x));
                y += constraints.getHeight().getValue();
                y += border;
                maxWidth = Math.max(maxWidth, constraints.getWidth().getValue());
                rows++;
            } while (y < height);
            x += maxWidth;
            x += border;
            for (Component component : getComponents()) {
                layout.getConstraints(component).setWidth(Spring.constant(maxWidth));
            }
            column++;
            row = topLeftCell.getCellIdentifier().getRow();
        } while (x < width);
    }

    public static void main(String[] args) {
        Frame f = new Frame();
        f.setSize(500, 500);
        Sheet s = new Sheet();
        f.add(s);
        f.addKeyListener(s);
        f.setVisible(true);
        s.init();
    }
}