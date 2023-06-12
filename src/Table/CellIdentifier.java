package Table;

public class CellIdentifier {
    private final String column;/// columns represented by A, B, ..., Z, AA, AB, ..., AZ, BA
    private final int row;

    public CellIdentifier(String column, int row) {
        this.column = column;
        this.row = row;
    }

    public CellIdentifier(int column, int row) {
        this.column = columnNumberToString(column);
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public int getColumnNumber() {
        return columnStringToNumber(column);
    }

    public int getRow() {
        return row;
    }

    static public int columnStringToNumber(String column) {
        int result = 0;
        for (char c : column.toCharArray()) {
            result = result * 26 + (c - 'A' + 1);
        }
        return result;
    }/// A - 1, B - 2, ..., Z - 26, AA - 27, AB - 28,... AZ - 52, BA - 53, ...

    static public String columnNumberToString(int column) {
        StringBuilder sb = new StringBuilder("");
        while (column > 0) {
            column--;
            char c = (char) ('A' + column % 26);
            sb.append(c);
            column /= 26;
        }
        return sb.reverse().toString();
    }/// A - 1, B - 2, ..., Z - 26, AA - 27, AB - 28,... AZ - 52, BA - 53, ...

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CellIdentifier))
            return false;
        CellIdentifier cid = (CellIdentifier) obj;
        return column.equals(cid.column) && row == cid.row;
    }

    @Override
    public int hashCode() {
        return (columnStringToNumber(column) << 16) | row; /// high 2 bytes are
        // for the column, and low 2 for row
        /*
         * this hashFunction enables up to 64k by 64k sheets
         * last "usable" column is 'CRXP' and last row is 65535
         * the ones after will "work" but the hash map responsible for keeping the
         * cells
         * will slow down
         */
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(column);
        sb.append(row);
        return sb.toString();
    }
}
