package JSON;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class JSONObject {

    private List<String> keys;
    private List<JSONObject> values;
    private Object value;
    private char type;

    public JSONObject() {
        super();
        keys = null;
        values = null;
        type = 'U'; /// Undefined object
        value = null;
    }

    public JSONObject(int i) {
        this();
        setValue(i);
    }

    public JSONObject(double d) {
        this();
        setValue(d);
    }

    public JSONObject(String s) {
        this();
        setValue(s);
    }

    public JSONObject(boolean b) {
        this();
        setValue(b);
    }

    public void setValue(int i) {
        if (type != 'D' && type != 'U')
            throw new JSONException("Error: cannot assign value of type 'I' to JSONObject of type '" + type + "'");
        value = Integer.valueOf(i);
        type = 'I';
    }

    public void setValue(double d) {
        if (type != 'D' && type != 'U')
            throw new JSONException("Error: cannot assign value of type 'D' to JSONObject of type '" + type + "'");
        value = Double.valueOf(d);
        type = 'D';
    }

    public void setValue(String s) {
        if (type != 'D' && type != 'U')
            throw new JSONException("Error: cannot assign value of type 'S' to JSONObject of type '" + type + "'");
        value = new String(s);
        type = 'S';
    }

    public void setValue(boolean b) {
        if (type != 'D' && type != 'U')
            throw new JSONException("Error: cannot assign value of type 'B' to JSONObject of type '" + type + "'");
        value = Boolean.valueOf(b);
        type = 'B';
    }

    public void setNull() {
        if (type != 'D' && type != 'U')
            throw new JSONException("Error: cannot assign value of type 'N' to JSONObject of type '" + type + "'");
        value = null;
        type = 'N';
    }

    public Object getValue() {
        return value;
    }

    public int getInt() {
        if (type != 'I')
            throw new JSONException("Error: invalid type '" + type + "', expected 'I'");
        return ((Integer) value).intValue();
    }

    public double getDouble() {
        if (type != 'D')
            throw new JSONException("Error: invalid type '" + type + "', expected 'D'");
        return ((Double) value).doubleValue();
    }

    public String getString() {
        if (type != 'S')
            throw new JSONException("Error: invalid type '" + type + "', expected 'S'");
        return (String) value;
    }

    public boolean getBoolean() {
        if (type != 'D')
            throw new JSONException("Error: invalid type '" + type + "', expected 'B'");
        return ((Boolean) value).booleanValue();
    }

    public char getType() {
        return type;
    }

    public boolean isEmpty() {
        if (type == 'A' || type == 'M')
            return values.isEmpty();
        throw new JSONException("Error: method 'isEmpty()' is not to be used with JSONObjects of type '" + type + "'");
    }

    public static JSONObject parseString(String string) {
        JSONObject currentContainer = new JSONObject();
        Stack<JSONObject> containerStack = new Stack<JSONObject>();
        containerStack.push(currentContainer);
        char currentChar = ' ';
        char type;
        int index = -1;
        while (!containerStack.empty()) {
            currentContainer = containerStack.pop();
            type = currentContainer.getType();
            /// skip all whitespace
            while ((currentChar == ' ' || currentChar == '\t' || currentChar == '\n') && index < string.length())
                currentChar = string.charAt(++index);
            if (type == 'U') {
                if (currentChar == '[') { /// array
                    type = 'A';
                    currentContainer.type = 'A';
                } else if (currentChar == '{') { /// map
                    type = 'M';
                    currentContainer.type = 'M';
                } else if ((currentChar >= '0' && currentChar <= '9') || currentChar == '-') { /// integer or double
                    int number = 0;
                    int decimals = -1;
                    if (currentChar == '-') {
                        if (index == string.length() - 1) {
                            throw new JSONException(index, string);
                        }
                        number = -(string.charAt(++index) - '0');
                    } else {
                        number = currentChar - '0';
                    }
                    while (index < string.length() - 1) {
                        currentChar = string.charAt(++index);
                        if (currentChar >= '0' && currentChar <= '9') {
                            int dig = currentChar - '0';
                            if (number < 0)
                                dig = -dig;
                            number = number * 10 + dig;
                        } else
                            break;
                    }
                    if (currentChar == '.') {
                        decimals = 0;
                        while (index < string.length() - 1) {
                            currentChar = string.charAt(++index);
                            if (currentChar >= '0' && currentChar <= '9') {
                                decimals = decimals * 10 + currentChar - '0';
                            } else {
                                break;
                            }
                        }
                    }
                    if (decimals == -1) {
                        currentContainer.setValue(number);
                    } else {
                        double d = number;
                        if (d < 0) {
                            decimals = -decimals;
                        }
                        d += decimals * (Math.pow(10, -(int) (Math.log10(decimals) + 1)));
                        currentContainer.setValue(d);
                    }
                    index--;
                } else if (currentChar == '"') { /// string
                    type = 'S';
                    int stringEnd = string.indexOf('\"', index + 1);
                    if (stringEnd == -1) {
                        throw new JSONException(
                                "Error: String block started but never finished at " + Integer.toString(index));
                    }
                    currentContainer.setValue(string.substring(index + 1, stringEnd));
                    index = stringEnd;
                } else if (currentChar == 'f' || currentChar == 't') { /// boolean
                    type = 'B';
                    if (index + 4 > string.length()) {
                        throw new JSONException(index, string);
                    }
                    String value = string.substring(index, index + 4);
                    if (value.equals("true")) {
                        currentContainer.setValue(true);
                        index += 4;
                    } else if (value.equals("fals")) {
                        index += 4;
                        if (index >= string.length() || string.charAt(index) != 'e')
                            throw new JSONException(index, string);
                        currentContainer.setValue(false);
                        index++;
                    } else {
                        throw new JSONException(index, string);
                    }
                } else if (currentChar == 'n') { /// nulll
                    type = 'N';
                    if (index + 4 < string.length() || !string.substring(index, index + 4).equals("null")) {
                        throw new JSONException(index, string);
                    }
                    currentContainer.setNull();
                } else {
                    throw new JSONException(index, string);
                }
                if (++index < string.length()) {
                    currentChar = string.charAt(index);
                    /// skip all following whitespace
                    while ((currentChar == ' ' || currentChar == '\t' || currentChar == '\n')
                            && index < string.length())
                        currentChar = string.charAt(++index);
                } else {

                }
            }
            type = currentContainer.getType();
            if (type == 'M') {
                if (currentChar == '"') { /// new entry
                    int nameEnd = string.indexOf('\"', index + 1);
                    String name = string.substring(index + 1, nameEnd);
                    JSONObject obj = new JSONObject();
                    currentContainer.add(name, obj);
                    containerStack.push(currentContainer);
                    containerStack.push(obj);
                    index = nameEnd + 1;
                    currentChar = string.charAt(index);
                    while ((currentChar == ' ' || currentChar == '\t' || currentChar == '\n')
                            && index < string.length())
                        currentChar = string.charAt(++index);
                    if (currentChar != ':')
                        throw new JSONException("Error: expected ':' but found '" + currentChar + "' at "
                                + Integer.toString(index) + " while parsing string:" + string);
                    currentChar = string.charAt(++index);
                    while ((currentChar == ' ' || currentChar == '\t' || currentChar == '\n')
                            && index < string.length())
                        currentChar = string.charAt(++index);
                } else if (currentChar == ',') {
                    currentChar = string.charAt(++index);
                    containerStack.push(currentContainer);
                } else if (currentChar == '}') { /// end of map
                    if (!containerStack.empty()) {
                        JSONObject parentContainer = containerStack.peek();
                        if (parentContainer.getType() == 'M' || parentContainer.getType() == 'A') {
                            parentContainer.add(currentContainer);
                        } else {
                            throw new JSONException(
                                    "Error: invalid JSONObject type '" + parentContainer.getType() + "'...");
                        }
                    }
                    index++;
                    if (index < string.length())
                        currentChar = string.charAt(index);
                } else {
                    containerStack.push(currentContainer);
                    containerStack.push(new JSONObject());
                }

            } else if (type == 'A') {
                if (currentChar == ',') {
                    currentChar = string.charAt(++index);
                    containerStack.push(currentContainer);
                    containerStack.push(new JSONObject());
                } else if (currentChar == ']') { /// end of array
                    if (!containerStack.empty()) {
                        JSONObject parentContainer = containerStack.peek();
                        if (parentContainer.getType() == 'M' || parentContainer.getType() == 'A') {
                            parentContainer.add(currentContainer);
                        } else {
                            throw new JSONException(
                                    "Error: invalid JSONObject type '" + parentContainer.getType() + "'...");
                        }
                    }
                    index++;
                    if (index < string.length())
                        currentChar = string.charAt(index);
                } else {
                    containerStack.push(currentContainer);
                    containerStack.push(new JSONObject());
                }
            } else if (type == 'I' || type == 'D' || type == 'S' || type == 'B' || type == 'N') {
                if (!containerStack.empty()) {
                    JSONObject parentContainer = containerStack.peek();
                    if (parentContainer.getType() == 'M' || parentContainer.getType() == 'A') {
                        parentContainer.add(currentContainer);
                    } else {
                        throw new JSONException(
                                "Error: invalid JSONObject type '" + parentContainer.getType() + "'...");
                    }
                }
            }
        }
        if (index != string.length()) {
            throw new JSONException(index, string);
        }
        return currentContainer;
    }

    public Iterator<String> keys() {
        if (type == 'M')
            return keys.iterator();
        throw new JSONException("Error: key iterator not available for JSONObject of type '" + type + "'");
    }

    public Iterator<JSONObject> values() {
        if (type == 'A' || type == 'M')
            return values.iterator();
        throw new JSONException("Error: key iterator not available for JSONObject of type '" + type + "'");

    }

    public String toString() {
        StringBuilder sb = new StringBuilder("");
        switch (type) {
            case 'I': /// integer
                sb.append((Integer) value);
                break;
            case 'D': /// double
                sb.append((Double) value);
                break;
            case 'S': /// string
                sb.append('"');
                sb.append((String) value);
                sb.append('"');
                break;
            case 'B': /// boolean
                sb.append((Boolean) value);
                break;
            case 'N': /// NULL
                sb.append("NULL");
                break;
            case 'A': /// array
                sb.append('[');
                for (JSONObject obj : values) {
                    sb.append(obj.toString());
                    sb.append(',');
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                sb.append(']');
                break;
            case 'M': /// map
                sb.append('{');
                for (int i = 0; i < keys.size(); i++) {
                    sb.append('"');
                    sb.append(keys.get(i));
                    sb.append("\":");
                    sb.append(values.get(i).toString());
                    sb.append(',');
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                sb.append('}');
                break;
            default:
                System.err.println("Invalid type: '" + type + "'...");
                break;
        }
        return sb.toString();
    }

    public void add(JSONObject value) {
        if (type != 'A' && type != 'M' && type != 'U') /// it is not an array or NULL
            throw new JSONException("Error: JSONObject is not of type array");

        if (type == 'U')
            type = 'A';
        if (values == null)
            values = new ArrayList<JSONObject>();
        if (type == 'A')
            values.add(value);
        if (type == 'M') /// sets the value of the newest member, only to be used in parseString
            values.set(values.size() - 1, value);
    }

    public void add(int i) {
        add(new JSONObject(i));
    }

    public void add(double d) {
        add(new JSONObject(d));
    }

    public void add(String s) {
        add(new JSONObject(s));
    }

    public void add(boolean b) {
        add(new JSONObject(b));
    }

    public void add(String key, JSONObject value) {
        if (type != 'M' && type != 'U')
            throw new JSONException("Error: JSONObject is not of type map...");

        if (type == 'U')
            type = 'M';
        if (keys == null)
            keys = new ArrayList<String>();
        if (values == null)
            values = new ArrayList<JSONObject>();
        keys.add(key);
        values.add(value);
    }

    public void add(String key, int i) {
        add(key, new JSONObject(i));
    }

    public void add(String key, double d) {
        add(key, new JSONObject(d));
    }

    public void add(String key, String s) {
        add(key, new JSONObject(s));
    }

    public void add(String key, boolean b) {
        add(key, new JSONObject(b));
    }

    public void addNull(String key) {
        JSONObject obj = new JSONObject();
        obj.setNull();
        add(key, obj);
    }

    public JSONObject get(String key) {
        if (type != 'M')
            throw new JSONException("Error: JSONObject is not of type map...");
        int index = keys.indexOf(key);
        if (index == -1)
            return null;
        return values.get(index);
    }

    public JSONObject get(int index) {
        if (type != 'A')
            throw new JSONException("Error: JSONObject is not of type map...");
        return values.get(index);
    }

    public static void main(String[] args) {
        String str = new String("[{\"a\":[1, 2], \"abc\":12}, \"test\"]");
        JSONObject obj = JSONObject.parseString(str);
        System.out.println(obj);
        System.out.println(obj.get(0).get("a").get(0));
        System.out.println(obj.get(0).get("a").get(1));
        System.out.println(obj.get(0).get("abc"));
        System.out.println(obj.get(1));
    }
}
