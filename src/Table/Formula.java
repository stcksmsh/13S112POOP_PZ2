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
    Cell parentCell;

    /// captures ([SHEETNAME]:)[COLUMN][ROW]
    private static Pattern cellPattern = Pattern.compile("(?:([a-zA-Z0-9]*):)?([A-Z]+[0-9]+)");

    /// captures FUNCTION((([SHEETNAME]:[COLUMN1][ROW1]:[COLUMN2][ROW2])))
    private static Pattern functionPattern = Pattern
            .compile("(?:([A-Z]+)\\((?:([A-Za-z0-9]+):)?([A-Z]+)([0-9]+):([A-Z]+)([0-9]+)\\))");

    public Formula() {
        displayValue = "";
        value = "";
        this.format = new NumberFormat(defaultPrecision);
        dependencies = new ArrayList<CellValue>();
    }

    public Formula(CellValue cellValue, Cell parentCell) {
        displayValue = "";
        this.value = "";
        this.parentCell = parentCell;
        this.parentSheet = (Sheet) parentCell.getParent();
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
        String prevDisplayValue = displayValue;
        evaluate();
        if (!displayValue.equals(prevDisplayValue))
            parentCell.updateLabel();
    }

    /// used to notify all dependencies that the formula no longer depends on them,
    /// used when the formula is changed to something else
    public void notifyDependencies() {
        for (CellValue cellValue : dependencies)
            cellValue.notifyDependency(this);
    }

    private CellValue getCellValue(String sheet, String cellID) {
        return parentSheet.getCellValue(sheet, cellID);
    }

    public CellValue getCellValueAndNotify(String sheet, String cellID) {
        return parentSheet.getCellValueAndNotify(this, sheet, cellID);
    }

    /// replaces all instances of cell references (e.g. A1) with their respective
    /// values (displayValues)
    public static String populateCellValues(Formula formula, String expression) {
        Matcher matcher = cellPattern.matcher(expression);
        while (matcher.find()) {
            String sheetName = matcher.group(1);
            String cellID = matcher.group(2);
            CellValue cellValue = formula.getCellValueAndNotify(sheetName, cellID);
            // String cellValue = getCellValue(sheetName, cellID);
            String cellDisplayValue = cellValue.getDisplayValue();
            if (cellDisplayValue.strip() == "") {
                throw new NullPointerException();
            }
            /// add the dependency
            formula.dependencies.add(cellValue);
            expression = expression.replaceFirst(matcher.group(), cellDisplayValue);
        }
        return expression;
    }

    /// evaluates the more complex multi-argument functions in a string
    /// (MAX, MIN, ...)
    public static String evaluateComplexFunctions(Formula formula, String expression) {
        Matcher matcher = functionPattern.matcher(expression);
        while (matcher.find()) {
            String function = matcher.group(1);
            String sheetName = matcher.group(2);
            String column1 = matcher.group(3);
            int row1 = Integer.parseInt(matcher.group(4));
            String column2 = matcher.group(5);
            int row2 = Integer.parseInt(matcher.group(6));
            expression = expression.replace(matcher.group(),
                    functionHandler(formula, function, sheetName, column1, row1, column2, row2));
        }
        return expression;
    }

    public static String functionHandler(Formula formula, String function, String sheetName, String column1, int row1,
            String column2, int row2) {
        String result = "";
        switch (function) {
            case "MAX":
                result = SheetFunctions.MAX(formula, sheetName, column1, row1, column2, row2);
                break;
            default:
                return "=ERROR=";
        }

        return result;
    }

    private void evaluate() {
        try {
            /// remove the '=' at the beginning of a formula
            displayValue = value.substring(1);

            /// remove all dependencies
            for (CellValue cellValue : dependencies) {
                cellValue.clearDependent(this);
            }
            dependencies.clear();

            displayValue = evaluateComplexFunctions(this, displayValue);

            displayValue = populateCellValues(this, displayValue);

            displayValue = Double.toString(eval(displayValue));
        } catch (NullPointerException npe) {
            displayValue = "=ERROR=";
        } catch (RuntimeException re) {
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
