package Table;

import java.util.ArrayList;

public class CellValue {
    protected String value;
    protected String displayValue;
    protected Format format;
    protected boolean error;
    protected ArrayList<Formula> dependents;/// list of formulas which reference this value

    public CellValue() {
        super();
        this.format = new TextFormat();
        displayValue = "";
        value = "";
        error = false;
        dependents = new ArrayList<Formula>();
    }

    public CellValue(String value) {
        super();
        this.value = value;
    }

    public CellValue(Formula formula) {
        dependents = formula.dependents;
    }

    public char getFormatCode() {
        return format.getCode();
    }

    public String getValue() {
        return value;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setFormat(Format format) {
        this.format = format;
        setValue(value);
        notifyDependents();
    }

    /// used to notify all dependents that the CellValue has changed
    protected void notifyDependents() {
        for (Formula formula : dependents)
            formula.notifyDependent();
    }

    /// used to notify the CellValue that a dependent is no longer dependent on it
    public void notifyDependency(Formula formula) {
        dependents.remove(formula);
    }

    public void setValue(String value) {
        this.value = value;
        displayValue = format.validate(value);
        if (displayValue.equals("=ERROR=")) {
            error = true;
            displayValue = "ERROR";
        } else {
            error = false;
        }
        notifyDependents();
    }

    public boolean isError() {
        return error;
    }
}
