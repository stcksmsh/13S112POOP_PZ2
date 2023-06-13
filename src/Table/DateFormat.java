package Table;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DateFormat extends Format {

    @Override
    public char getCode() {
        return 'D';
    }

    @Override
    public String validate(String text) {
        Pattern pattern = Pattern.compile("^(\\d{2})\\.(\\d{2})\\.(\\d{4})$");
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            return "=ERROR=";
        }
        int day = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int year = Integer.parseInt(matcher.group(3));

        if (month > 12 || month < 1 || day < 1)
            return "=ERROR=";
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            if (day > 31)
                return "=ERROR=";
        } else if (month == 2) {
            if (day > 29)
                return "=ERROR=";
            boolean isLeapYear = year % 4 == 0 && year % 100 != 0;
            if (!isLeapYear && day > 28)
                return "=ERROR=";
        } else {
            if (day > 30 || day < 1)
                return "=ERROR=";
        }
        return text;
    }

    public static void main(String[] args) {
        System.err.println(new DateFormat().validate("29.02.1100"));
    }
}
