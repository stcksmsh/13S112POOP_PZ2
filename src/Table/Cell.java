package Table;

import java.awt.Color;
import java.awt.Label;

public class Cell extends Label {
    private final CellIdentifier id;
    private CellValue value;
    private Format format;
    private boolean error;

    private static final Color errorColor = Color.RED;
    private static final Color focusErrorColor = new Color(128, 0, 0);
    private static final Color color = Color.white;
    private static final Color focusColor = color.GRAY;

    public Cell(CellIdentifier id) {
        super();
        this.id = id;
        value = new CellValue();
        format = new TextFormat();
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
        }
        setText(sb.toString()); /// 5 blank spaces is default
    }

    public void setFormat(Format f) {
        format = f;
        setValue(getValue());
    }

    public String getValue() {
        return value.getValue();
    }

    public void setValue(String text) {
        value.setValue(format.validate(text));
        if (value.getDisplayValue() == "=ERROR=") {
            error = true;
            setText("ERROR");
        } else {
            setText(value.getDisplayValue());
            error = false;
        }
        focus();
    }

    public void focus() {
        if (error) {
            setBackground(focusErrorColor);
        } else {
            setBackground(focusColor);
        }
    }

    public void unfocus() {
        if (error) {
            setBackground(errorColor);
        } else {
            setBackground(color);
        }
    }

    public CellIdentifier getCellIdentifier() {
        return id;
    }

}
