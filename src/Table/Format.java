package Table;

abstract public class Format {
    public Format() {
    };

    abstract public String getCode();

    abstract public String validate(String text);
}
