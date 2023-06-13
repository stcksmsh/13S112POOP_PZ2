package Table;

public class TextFormat extends Format {

    @Override
    public char getCode() {
        return 'T';
    }

    @Override
    public String validate(String text) {
        if (text.length() > 0) {
            if (text.charAt(0) == '=')
                return "=ERROR=";
            return text;
        }
        return text;
    }

}
