package Table;

public class Action {
    private String sheetName;
    private String oldValue;
    private String newValue;
    private String column;
    private int row;
    private String formatCode;

    public Action(String sheetName, String oldValue, String newValue, String column, int row, String formatCode) {
        this.sheetName = sheetName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.column = column;
        this.row = row;
        this.formatCode = formatCode;
    }

    public void undo(Table table) {
        table.setValue(sheetName, column, row, oldValue, formatCode);
    }

    public void redo(Table table) {
        table.setValue(sheetName, column, row, newValue, formatCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Action))
            return false;
        Action action = (Action) obj;
        if (sheetName != action.sheetName)
            return false;
        if (oldValue != action.oldValue)
            return false;
        if (newValue != action.newValue)
            return false;
        if (column != action.column)
            return false;
        if (row != action.row)
            return false;
        if (formatCode != action.formatCode)
            return false;
        return true;
    }
}
