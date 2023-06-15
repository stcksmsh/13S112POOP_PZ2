package Table;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;

public class Formula extends CellValue {

    private final static int defaultPrecision = 2;

    private ArrayList<CellValue> dependencies;

    Sheet parentSheet;

    public Formula() {
        displayValue = "";
        value = "";
        this.format = new NumberFormat(defaultPrecision);
        dependencies = new ArrayList<CellValue>();
    }

    public Formula(CellValue cellValue, Sheet parenSheet) {
        displayValue = "";
        this.value = "";
        this.parentSheet = parenSheet;
        format = new NumberFormat(defaultPrecision);
        dependencies = new ArrayList<CellValue>();
    }

    public Formula(String value) {
        displayValue = "";
        this.value = "";
        format = new NumberFormat(defaultPrecision);
        dependencies = new ArrayList<CellValue>();
        setValue(value);
    }

    /// used to notify formula of changes in its dependencies
    public void notifyDependent() {
        evaluate();
    }

    /// used to notify all dependencies that the formula no longer depends on them,
    /// used when the formula is changed to something else
    public void notifyDependencies() {
        for (CellValue cellValue : dependencies)
            cellValue.notifyDependency(this);
    }

    private String getCellValue(String sheet, String cellID) {
        return parentSheet.getCellValue(sheet, cellID);
    }

    private void evaluate() {
        try {
            displayValue = value.substring(1);
            Matcher matcher = Pattern.compile("(?:([a-zA-Z0-9]*):)?([A-Z]+[0-9]+)").matcher(value);
            while (matcher.find()) {
                String sheetName = matcher.group(1);
                String cellID = matcher.group(2);
                String cellValue = getCellValue(sheetName, cellID);
                displayValue = displayValue.replace(matcher.group(), getCellValue(sheetName, cellID));
            }

            displayValue = Double.toString(eval(displayValue));
        } catch (NullPointerException npe) {
            displayValue = "=ERROR=";
        }
        displayValue = format.validate(displayValue);
        if (displayValue.equals("=ERROR=")) {
            error = true;
            displayValue = "ERROR";
        } else {
            error = false;
        }
        notifyDependents();
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ')
                    nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length())
                    throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            // | functionName `(` expression `)` | functionName factor
            // | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+'))
                        x += parseTerm(); // addition
                    else if (eat('-'))
                        x -= parseTerm(); // subtraction
                    else
                        return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*'))
                        x *= parseFactor(); // multiplication
                    else if (eat('/'))
                        x /= parseFactor(); // division
                    else
                        return x;
                }
            }

            double parseFactor() {
                if (eat('+'))
                    return +parseFactor(); // unary plus
                if (eat('-'))
                    return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')'))
                        throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.')
                        nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z')
                        nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')'))
                            throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt"))
                        x = Math.sqrt(x);
                    else if (func.equals("sin"))
                        x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos"))
                        x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan"))
                        x = Math.tan(Math.toRadians(x));
                    else
                        throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^'))
                    x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    @Override
    public void setValue(String value) {
        this.value = value;
        evaluate();
    }
}
