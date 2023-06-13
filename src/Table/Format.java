package Table;

abstract public class Format {
    public Format() {
    };

    abstract public char getCode();

    abstract public String validate(String text);
}
