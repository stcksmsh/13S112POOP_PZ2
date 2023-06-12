package Table;

import java.awt.Color;
import java.awt.Label;

public class Cell extends Label {
    private final CellIdentifier id;
    private CellValue value;
    private Format format;

    public Cell(CellIdentifier id) {
        super();
        this.id = id;
        value = new CellValue();
        format = new Format();
        setAlignment(CENTER);
        setBackground(Color.DARK_GRAY);
        setForeground(Color.WHITE);
        setAlignment(CENTER);
        StringBuilder sb = new StringBuilder(""); /// id.getColumn()
        if (id.getColumnNumber() == 0) {
            if (id.getRow() == 0) {
                sb.append("*");
            } else {
                sb.append(id.getRow());
            }
        } else if (id.getRow() == 0) {
            sb.append(id.getColumn());
        } else {
            setForeground(Color.BLACK);
            setBackground(Color.WHITE);
        }
        setText(sb.toString()); /// 5 blank spaces is default
    }

    public Cell(String column, int row) {
        super();
        id = new CellIdentifier(column, row);
        value = new CellValue();
        format = new Format();
        setAlignment(CENTER);
        setBackground(Color.WHITE);
        setText("     ");
    }

    public CellIdentifier getCellIdentifier() {
        return id;
    }

}
