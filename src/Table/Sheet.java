package Table;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.SpringLayout;
import javax.swing.Spring;
import javax.swing.SpringLayout.Constraints;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Sheet extends Panel
        implements MouseListener, MouseWheelListener, Iterable<Cell> {

    private static int border = 3; /// size of border between adjacent cells

    /// the HashMap containing all of the cells
    private Map<CellIdentifier, Cell> cells = new HashMap<CellIdentifier, Cell>();

    private Cell focusedCell; /// the cell currently being focused
    private Cell topLeftCell; /// the cell in the top left corner of the sheet
    private Cell bottomRightCell; /// the fully visible cell in the bottom right corner of the sheet

    public Sheet() {
        super();
        createCell(0, 0);
        createCell(1, 0);
        createCell(0, 1);
        focusedCell = createCell(1, 1);
        topLeftCell = focusedCell;
        bottomRightCell = topLeftCell;
        focusedCell.setBackground(Color.GRAY);
        setLayout(new SpringLayout());
        setVisible(true);
        setFocusable(true);
    }

    @Override
    public Iterator<Cell> iterator() {
        return cells.values().iterator();
    }

    public void init() {
        makeSheet();
        changeFocus(focusedCell);
    }

    public final Map<CellIdentifier, Cell> getCells() {
        return cells;
    }

    private Cell createCell(int column, int row) {
        CellIdentifier id = new CellIdentifier(CellIdentifier.columnNumberToString(column), row);
        Cell cell = new Cell(id);
        cells.put(id, cell);
        cell.addMouseListener(this);
        cell.addMouseWheelListener(this);
        return cell;
    }

    private void makeSheet() { /// tunes the SpringLayout of the Sheet
        removeAll(); /// clears the grid from all of its components, they will be added again
        makeCompactGrid();
        revalidate();
    }

    public void setFormat(Format f) {
        focusedCell.setFormat(f);
        makeSheet();
    }

    private void changeFocus(Cell cell) {
        focusedCell.unfocus();
        focusedCell = cell;
        focusedCell.focus();
    }

    public Cell getFocusedCell() {
        return focusedCell;
    }

    public void keyPress(KeyEvent e) {
        CellIdentifier focusedCellIdentifier = focusedCell.getCellIdentifier();
        int focusedColumn = CellIdentifier.columnStringToNumber(focusedCellIdentifier.getColumn());
        int focusedRow = focusedCellIdentifier.getRow();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (e.isControlDown()) {
                    focusedRow = 1;
                } else if (focusedRow > 1) {
                    focusedRow--;
                }
                e.consume();
                break;
            case KeyEvent.VK_DOWN:
                if (e.isControlDown()) {
                    focusedRow = bottomRightCell.getCellIdentifier().getRow();
                } else {
                    focusedRow++;
                }
                e.consume();
                break;
            case KeyEvent.VK_LEFT:
                if (e.isAltDown())
                    return;
                if (e.isControlDown()) {
                    focusedColumn = 1;
                } else if (focusedColumn > 1) {
                    focusedColumn--;
                }
                e.consume();
                break;
            case KeyEvent.VK_RIGHT:
                if (e.isAltDown())
                    return;
                if (e.isControlDown()) {
                    focusedColumn = bottomRightCell.getCellIdentifier().getColumnNumber();
                } else {
                    focusedColumn++;
                }
                e.consume();
                break;
        }

        String column = CellIdentifier.columnNumberToString(focusedColumn);

        if (focusedColumn < topLeftCell.getCellIdentifier().getColumnNumber()) {
            topLeftCell = cells.get(new CellIdentifier(focusedColumn, topLeftCell.getCellIdentifier().getRow()));
            if (topLeftCell == null)
                topLeftCell = createCell(focusedColumn, topLeftCell.getCellIdentifier().getRow());
            makeSheet();
        }
        if (focusedRow < topLeftCell.getCellIdentifier().getRow()) {
            topLeftCell = cells.get(new CellIdentifier(topLeftCell.getCellIdentifier().getColumnNumber(), focusedRow));
            if (topLeftCell == null)
                topLeftCell = createCell(topLeftCell.getCellIdentifier().getColumnNumber(), focusedRow);
            makeSheet();
        }
        if (focusedColumn >= bottomRightCell.getCellIdentifier().getColumnNumber()) {
            topLeftCell = cells.get(new CellIdentifier(topLeftCell.getCellIdentifier().getColumnNumber() + 1,
                    topLeftCell.getCellIdentifier().getRow()));
            if (topLeftCell == null)
                topLeftCell = createCell(topLeftCell.getCellIdentifier().getColumnNumber() + 1,
                        topLeftCell.getCellIdentifier().getRow());
            makeSheet();
        }
        if (focusedRow >= bottomRightCell.getCellIdentifier().getRow()) {
            topLeftCell = cells.get(new CellIdentifier(topLeftCell.getCellIdentifier().getColumnNumber(),
                    topLeftCell.getCellIdentifier().getRow() + 1));
            if (topLeftCell == null)
                topLeftCell = createCell(topLeftCell.getCellIdentifier().getColumnNumber(),
                        topLeftCell.getCellIdentifier().getRow() + 1);
            makeSheet();
        }
        Cell newFocus = cells.get(new CellIdentifier(column, focusedRow));
        if (newFocus == null) {
            newFocus = createCell(CellIdentifier.columnStringToNumber(column), focusedRow);
        }
        changeFocus(newFocus);
    }

    public void setValue(String column, int row, String value, String formatCode) {
        CellIdentifier cellIdentifier = new CellIdentifier(column, row);
        Cell cell = cells.get(cellIdentifier);
        cell.setValue(value);
        Format format = null;
        if (formatCode.equals("T")) {
            format = new TextFormat();
        } else if (formatCode.equals("D")) {
            format = new DateFormat();
        } else {
            int precision = Integer.parseInt(formatCode.substring(1));
            format = new NumberFormat(precision);
        }
        cell.setFormat(format);
    }

    public void setText(String text) {
        focusedCell.setValue(text);
        makeSheet();
    }

    public String getCellValue(String sheetName, String cellID) {
        if (sheetName == null) {
            return cells.get(new CellIdentifier(cellID)).getDisplayValue();
        } else {
            return ((Table) getParent()).getCellValue(sheetName, cellID);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object obj = e.getSource();
        if (obj instanceof Cell)
            changeFocus((Cell) obj);
        dispatchEvent(e);
    }

    public Cell getCell(CellIdentifier cellIdentifier) {
        return cells.getOrDefault(cellIdentifier,
                createCell(cellIdentifier.getColumnNumber(), cellIdentifier.getRow()));
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
                if (topLeftCell == null)
                    topLeftCell = createCell(topLeftId.getColumnNumber(), row);
                makeSheet();
            }
        } else {
            CellIdentifier topLeftId = topLeftCell.getCellIdentifier();
            int row = topLeftId.getRow();
            row++;
            CellIdentifier newTopLeftId = new CellIdentifier(topLeftId.getColumn(), row);
            topLeftCell = cells.get(newTopLeftId);
            if (topLeftCell == null)
                createCell(topLeftId.getColumnNumber(), row);
            makeSheet();
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

    public void makeCompactGrid() {
        SpringLayout layout = (SpringLayout) this.getLayout();
        /// X and Y coordinate counters
        int x = border;
        int y = border;

        /// height and width of the container
        int height = getHeight();
        int width = getWidth();

        /// row and column of cell being looked at currently
        int column = 0;
        int row = 0;

        /// bottom right column and row
        int lastColumn = 0;
        int lastRow = 0;

        int maxWidth;
        do {
            maxWidth = 20;
            y = border;
            int i = getComponentCount();
            do {
                Cell cell = cells.get(new CellIdentifier(column, row));
                if (cell == null) {
                    cell = createCell(column, row);
                }
                if (row == 0) {
                    row = topLeftCell.getCellIdentifier().getRow();
                } else {
                    row++;
                }
                add(cell);
                Constraints constraints = layout.getConstraints(cell);
                maxWidth = Math.max(maxWidth, constraints.getWidth().getValue());
                constraints.setY(Spring.constant(y));
                constraints.setX(Spring.constant(x));
                y += constraints.getHeight().getValue();
                y += border;
            } while (y < height);
            if (column == 0) {
                column = topLeftCell.getCellIdentifier().getColumnNumber();
            } else {
                column++;
            }
            x += maxWidth;
            x += border;
            lastRow = row;
            for (; i < getComponentCount(); i++) {
                Component component = getComponent(i);
                layout.getConstraints(component).setWidth(Spring.constant(maxWidth));
            }
            row = 0;
        } while (x < width);
        lastColumn = column;
        lastColumn--;
        lastRow--;
        bottomRightCell = cells.get(new CellIdentifier(lastColumn, lastRow));
    }
}