package Table;

public class NumberFormat extends Format {
    private int precision;

    public NumberFormat(int precision) {
        super();
        this.precision = precision;
    }

    @Override
    public String getCode() {
        StringBuilder sb = new StringBuilder("N");
        sb.append(precision);
        return sb.toString();
    }

    @Override
    public String validate(String text) {
        try {
            text = String.format("%." + precision + "f", Double.parseDouble(text));
            return text;
        } catch (NumberFormatException nfe) {
            return "=ERROR=";
        }
    }
}
