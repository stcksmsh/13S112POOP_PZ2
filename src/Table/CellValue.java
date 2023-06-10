package Table;

public class CellValue {
    String value;

    CellValue() {
        super();
        value = "";
    }

    CellValue(String value) {
        super();
        this.value = value;
    }

    public String getDisplayValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
