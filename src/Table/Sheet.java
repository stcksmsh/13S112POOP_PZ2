package Table;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.ProcessBuilder.Redirect;

import javax.swing.*;
import javax.swing.SpringLayout.Constraints;

import java.util.HashMap;

public class Sheet extends JPanel implements MouseListener, MouseWheelListener, KeyListener {

    private static int border = 3; /// size of border between adjacent cells

    /// the HashMap containing all of the cells
    private HashMap<CellIdentifier, Cell> cells = new HashMap<CellIdentifier, Cell>();

    private Cell focusedCell; /// the cell currently being focused
    private Cell topLeftCell; /// the cell in the top left corner of the sheet
    private Cell bottomRightCell; /// the fully visible cell in the bottom right corner of the sheet

    public Sheet() {
        super();
        createCell(1, 1);
        focusedCell = cells.get(new CellIdentifier("A", 1));
        topLeftCell = focusedCell;
        bottomRightCell = topLeftCell;
        focusedCell.setBackground(Color.GRAY);
        setBackground(Color.black);
        setLayout(new SpringLayout());
        setOpaque(true);
    }

    public void init() {
        makeSheet();
        changeFocus(focusedCell);
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
    }

    private void changeFocus(Cell cell) {
        focusedCell.setBackground(Color.WHITE);
        focusedCell = cell;
        focusedCell.setBackground(Color.GRAY);
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
        CellIdentifier focusedCellIdentifier = focusedCell.getCellIdentifier();
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

        if (focusedColumn < topLeftCell.getCellIdentifier().getColumnNumber()) {
            topLeftCell = cells.get(new CellIdentifier(focusedColumn, topLeftCell.getCellIdentifier().getRow()));
            makeSheet();
        }
        if (focusedRow < topLeftCell.getCellIdentifier().getRow()) {
            topLeftCell = cells.get(new CellIdentifier(topLeftCell.getCellIdentifier().getColumnNumber(), focusedRow));
            makeSheet();
        }
        if (focusedColumn >= bottomRightCell.getCellIdentifier().getColumnNumber()) {
            topLeftCell = cells.get(new CellIdentifier(topLeftCell.getCellIdentifier().getColumnNumber() + 1,
                    topLeftCell.getCellIdentifier().getRow()));
            makeSheet();
        }
        if (focusedRow >= bottomRightCell.getCellIdentifier().getRow()) {
            topLeftCell = cells.get(new CellIdentifier(topLeftCell.getCellIdentifier().getColumnNumber(),
                    topLeftCell.getCellIdentifier().getRow() + 1));
            makeSheet();
        }
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
        int x = border;
        int y = border;

        /// height and width of the container
        int height = getHeight();
        int width = getWidth();

        /// row and column of cell being looked at currently
        int column = topLeftCell.getCellIdentifier().getColumnNumber();
        int row = topLeftCell.getCellIdentifier().getRow();

        /// bottom right column and row
        int lastColumn = 0;
        int lastRow = 0;

        int maxWidth = 0;

        do {
            y = border;
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
            } while (y < height);
            column++;
            x += maxWidth;
            x += border;
            for (Component component : getComponents()) {
                layout.getConstraints(component).setWidth(Spring.constant(maxWidth));
            }
            lastRow = row;
            row = topLeftCell.getCellIdentifier().getRow();
        } while (x < width);
        lastColumn = column;
        // System.out.println(CellIdentifier.columnNumberToString(lastColumn));
        // System.out.println(lastRow);
        lastColumn--;
        lastRow--;
        bottomRightCell = cells.get(new CellIdentifier(lastColumn, lastRow));
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