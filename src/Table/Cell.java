package Table;

import java.awt.Label;

public class Cell extends Label {
    CellIdentifier id;
    private CellValue value;
    private Format format;

    public Cell(String column, int row) {
        super();
        id = new CellIdentifier(column, row);
        value = new CellValue();
        format = new Format();
        setAlignment(CENTER);
        setText("tuvwxyz");
    }
}