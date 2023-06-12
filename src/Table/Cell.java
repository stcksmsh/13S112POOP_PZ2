package Table;

import java.awt.Color;
import java.awt.Label;

public class Cell extends Label {
    CellIdentifier id;
    private CellValue value;
    private Format format;

    public Cell(CellIdentifier id) {
        super();
        this.id = id;
        value = new CellValue();
        format = new Format();
        setAlignment(CENTER);
        setBackground(Color.WHITE);
        setText("     "); /// 5 blank spaces is default
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
