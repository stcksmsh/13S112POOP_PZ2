package Table;

public class SheetFunctions {
    public static String MAX(Formula formula, String sheetName, String column1, int row1, String column2, int row2) {
        int column1Number = CellIdentifier.columnStringToNumber(column1);
        int column2Number = CellIdentifier.columnStringToNumber(column2);
        if (row1 > row2 || column1Number > column2Number)
            return "=ERROR=";/// the second cell is above or to the left of the first
        double max = Double.MIN_VALUE;
        for (int row = row1; row <= row2; row++) {
            for (int columnNumber = column1Number; columnNumber <= column2Number; columnNumber++) {
                String cellValue = formula
                        .getCellValueAndNotify(sheetName,
                                CellIdentifier.columnNumberToString(columnNumber) + Integer.toString(row))
                        .getDisplayValue();
                double value = Double.MIN_VALUE;
                if (cellValue != "") {
                    try {
                        value = Double.parseDouble(cellValue);
                    } catch (NumberFormatException nfe) {
                        value = Double.MIN_VALUE;
                    }
                }
                max = Math.max(value, max);
            }
        }
        return Double.toString(max);
    }
}
