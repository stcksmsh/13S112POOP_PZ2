package Table;

import java.awt.Color;
import java.awt.Label;

public class Cell extends Label {
    private final CellIdentifier id;
    private CellValue value;

    private static final Color errorColor = Color.RED;
    private static final Color focusErrorColor = new Color(128, 0, 0);
    private static final Color color = Color.white;
    private static final Color focusColor = color.GRAY;

    public Cell(CellIdentifier id) {
        super();
        this.id = id;
        value = new CellValue();
        setAlignment(CENTER);
        setBackground(Color.DARK_GRAY);
        setForeground(Color.WHITE);
        setAlignment(CENTER);
        StringBuilder sb = new StringBuilder(""); /// id.getColumn()
        if (id.getColumnNumber() == 0) {
            if (id.getRow() == 0) {
                sb.append("*");
            } else {
                sb.append(id.getRow());
            }
        } else if (id.getRow() == 0) {
            sb.append(id.getColumn());
        } else {
            setForeground(Color.BLACK);
            setBackground(color);
            sb.append(value.getDisplayValue());
        }
        setText(sb.toString()); /// 5 blank spaces is default
    }

    public void setFormat(Format f) {
        /// cant set formula to anything other than NumberFormat
        if (value instanceof Formula && !(f instanceof NumberFormat))
            return;
        value.setFormat(f);
        setValue(getValue());
    }

    public String getFormatCode() {
        return value.getFormatCode();
    }

    public String getValue() {
        return value.getValue();
    }

    public void updateLabel() {
        setText(value.getDisplayValue());
        unfocus();
    }

    public void setValue(String text) {
        if (text.length() > 0 && text.charAt(0) == '=') {/// formula
            if (value instanceof Formula) {
                value.setValue(text);
            } else {
                value = new Formula(value, this);
                value.setValue(text);
            }
        } else {
            if (value instanceof Formula) {
                ((Formula) value).notifyDependencies();
                value = new CellValue();
                value.setValue(text);
            } else {
                value.setValue(text);
            }
        }
        setText(value.getDisplayValue());
    }

    public String getDisplayValue() {
        return value.getDisplayValue();
    }

    public void focus() {
        if (value.isError()) {
            setBackground(focusErrorColor);
        } else {
            setBackground(focusColor);
        }
    }

    public void unfocus() {
        if (value.isError()) {
            setBackground(errorColor);
        } else {
            setBackground(color);
        }
    }

    public CellValue getCellValueAndNotify(Formula source) {
        value.addDependent(source);
        return value;
    }

    public CellValue getCellValue() {
        return value;
    }

    public CellIdentifier getCellIdentifier() {
        return id;
    }

}
