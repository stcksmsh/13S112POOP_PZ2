package Table;

public class CellIdentifier {
    private String column;/// columns represented by A, B, ..., Z, AA, AB, ..., AZ, BA
    private int row;

    public CellIdentifier(String column, int row) {
        this.column = column;
        this.row = row;
    }

    static public int getColumnNumber(String column) {
        int result = 0;
        for (char c : column.toCharArray()) {
            result = result * 26 + (c - 'A' + 1);
        }
        return result - 1;
    }/// A - 1, B - 2, ..., Z - 26, AA - 27, AB - 28,... AZ - 52, BA - 53, ...

    static public String getColumnString(int column) {
        StringBuilder sb = new StringBuilder();
        column++;
        while (column > 0) {
            char c = (char) ('A' + column % 26 - 1);
            if (c < 'A')
                c = 'Z';
            sb.append(c);
            column /= 26;
        }
        return sb.reverse().toString();
    }/// A - 1, B - 2, ..., Z - 26, AA - 27, AB - 28,... AZ - 52, BA - 53, ...

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CellIdentifier) {
            CellIdentifier cell = (CellIdentifier) obj;
            return column.equals(cell.column) && row == cell.row;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getColumnNumber(column) * (2 << 16) + row; /// high 2 bytes are for the column, and low 2 for row
        /// this hashFunction enables up to 64k by 64k sheets
    }
}
