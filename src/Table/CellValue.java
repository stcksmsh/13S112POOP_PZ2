package Table;

public class CellValue {
    protected String value;
    protected String displayValue;

    CellValue() {
        super();
        value = "";
    }

    CellValue(String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
