package JSOpeN;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author Kosta Vukicevic
 * @version v0.1-alpha
 */
public class JSONObject {

    /**
     * List of names in the JSONObject, if it is an array or a value, it will be
     * null
     */
    private List<String> names;
    /**
     * List of values in the JSONObject, if it is a value it will be null
     */
    private List<JSONObject> values;
    /**
     * The value of the JSONObject, if it is an array or map, will be null
     */
    private Object value;
    /**
     * The type of the JSONObject, it may be:
     * 'U' - Undefined
     * 'M' - Map, a collection of name/value pairs
     * 'A' - Array, an ordered list of values
     * 'I' - Integer
     * 'D' - Double
     * 'S' - String
     * 'B' - Boolean
     * 'N' - Null
     */
    private char type;
    /**
     * The string used for indentation in {@link JSONObject#toBeautifulString(int)
     * toBeautifulString}
     */
    private static final String indent = "  ";

    /**
     * Creates a new JSONObject of type 'U'
     */
    public JSONObject() {
        super();
        names = null;
        values = null;
        type = 'U'; /// Undefined object
        value = null;
    }

    /**
     * Creates a new JSONObject of type 'I'
     * 
     * @param i the value of the new JSONObject
     */
    public JSONObject(int i) {
        this();
        setValue(i);
    }

    /**
     * Creates a new JSONObject of type 'D'
     * 
     * @param d the value of the new JSONObject
     */
    public JSONObject(double d) {
        this();
        setValue(d);
    }

    /**
     * Creates a new JSONObject of type 'S'
     * 
     * @param s the value of the new JSONObject
     */
    public JSONObject(String s) {
        this();
        setValue(s);
    }

    /**
     * Creates a new JSONObject of type 'B'
     * 
     * @param b the value of the new JSONObject
     */
    public JSONObject(boolean b) {
        this();
        setValue(b);
    }

    /**
     * Creates a new JSONObject of type 'N'
     * 
     * @return a null JSONObject
     */
    public static JSONObject nullObject() {
        JSONObject nullObject = new JSONObject();
        nullObject.type = 'N';
        return nullObject;
    }

    /**
     * @param i the new value of the JSONObject
     * @throws JSONException if the JSONObject is of type 'A' or 'M'
     */
    public void setValue(int i) {
        if (type == 'M' || type == 'A')
            throw new JSONException("Error: cannot assign value of type 'I' to JSONObject of type '" + type + "'");
        value = Integer.valueOf(i);
        type = 'I';
    }

    /**
     * @param d the new value of the JSONObject
     * @throws JSONException if the JSONObject is of type 'A' or 'M'
     */
    public void setValue(double d) {
        if (type == 'M' || type == 'A')
            throw new JSONException("Error: cannot assign value of type 'D' to JSONObject of type '" + type + "'");
        value = Double.valueOf(d);
        type = 'D';
    }

    /**
     * @param s the new value of the JSONObject
     * @throws JSONException if the JSONObject is of type 'A' or 'M'
     */
    public void setValue(String s) {
        if (type == 'M' || type == 'A')
            throw new JSONException("Error: cannot assign value of type 'S' to JSONObject of type '" + type + "'");
        value = new String(s);
        type = 'S';
    }

    /**
     * @param b the new value of the JSONObject
     * @throws JSONException if the JSONObject is of type 'A' or 'M'
     */
    public void setValue(boolean b) {
        if (type == 'M' || type == 'A')
            throw new JSONException("Error: cannot assign value of type 'B' to JSONObject of type '" + type + "'");
        value = Boolean.valueOf(b);
        type = 'B';
    }

    /**
     * Sets the value of the JSONObject to null
     * 
     * @throws JSONException if the JSONObject is of type 'A' or 'M'
     */
    public void setNull() {
        if (type == 'M' || type == 'A')
            throw new JSONException("Error: cannot assign value of type 'N' to JSONObject of type '" + type + "'");
        value = null;
        type = 'N';
    }

    /**
     * @return the value of the JSONObject
     * @throws JSONException if the JSONObject is of type 'A' or 'M'
     */
    public Object getValue() {
        if (type == 'M' || type == 'A')
            throw new JSONException("Error: cannot get value of JSONObject of type " + type + "");
        return value;
    }

    /**
     * @return the value of the JSONObject as an int
     * @throws JSONException if the JSONObject is not of type 'I'
     */
    public int getInt() {
        if (type != 'I')
            throw new JSONException("Error: invalid type '" + type + "', expected 'I'");
        return ((Integer) value).intValue();
    }

    /**
     * @return the value of the JSONObject as an double
     * @throws JSONException if the JSONObject is not of type 'D'
     */
    public double getDouble() {
        if (type != 'D')
            throw new JSONException("Error: invalid type '" + type + "', expected 'D'");
        return ((Double) value).doubleValue();
    }

    /**
     * @return the value of the JSONObject as a string
     * @throws JSONException if the JSONObject is not of type 'S'
     */
    public String getString() {
        if (type != 'S')
            throw new JSONException("Error: invalid type '" + type + "', expected 'S'");
        return (String) value;
    }

    /**
     * @return the value of the JSONObject as a boolean
     * @throws JSONException if the JSONObject is not of type 'B'
     */
    public boolean getBoolean() {
        if (type != 'B')
            throw new JSONException("Error: invalid type '" + type + "', expected 'B'");
        return ((Boolean) value).booleanValue();
    }

    /**
     * @return the type of the JSONObject
     */
    public char getType() {
        return type;
    }

    /**
     * @return whether the JSONObject is empty
     * @throws JSONException if the JSONObject is not of type 'A' or 'M'
     */
    public boolean isEmpty() {
        if (type == 'A' || type == 'M')
            return values.isEmpty();
        throw new JSONException("Error: method 'isEmpty()' is not to be used with JSONObjects of type '" + type + "'");
    }

    /**
     * Parses the given string into a new JSONObject
     * 
     * @param string the JSON string to parse into a JSONObject
     * @return the parsed JSONObject
     * @throws JSONException if the string is blank or not a proper JSON string in
     *                       any way
     */
    public static JSONObject parseString(String string) {
        if (string.isBlank())
            throw new JSONException("Error: the given string is blank");
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
                    if (index + 4 > string.length())
                        throw new JSONException(index, string);
                    String value = string.substring(index, index + 4);
                    if (value.equals("true")) {
                        currentContainer.setValue(true);
                        index += 3;
                    } else if (value.equals("fals")) {
                        index += 4;
                        if (index >= string.length() || string.charAt(index) != 'e')
                            throw new JSONException(index, string);
                        currentContainer.setValue(false);
                    } else {
                        throw new JSONException(index, string);
                    }
                } else if (currentChar == 'n') { /// nulll
                    type = 'N';
                    if (index + 4 >= string.length() || !string.substring(index, index + 4).equals("null")) {
                        System.err.println(string.length());
                        throw new JSONException(index, string);
                    }
                    currentContainer.setNull();
                    index += 3;
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

    /**
     * @return an iterator of names in the JSONObject
     * @throws JSONException if the JSONObject is not of type 'M'
     */
    public Iterator<String> names() {
        if (type != 'M')
            throw new JSONException("Error: name iterator not available for JSONObject of type '" + type + "'");
        return names.iterator();
    }

    /**
     * @return an iterator of the values in the JSONObject
     * @throws JSONException if the JSONObject is not of type 'A' or 'M'
     */
    public Iterator<JSONObject> values() {
        if (type != 'A' && type != 'M')
            throw new JSONException("Error: name iterator not available for JSONObject of type '" + type + "'");
        return values.iterator();
    }

    /**
     * @return an iterator of the name\value pairs in the JSONObject
     * @throws JSONException if the JSONObject is not of type 'M'
     */
    public Iterator<Map.Entry<String, JSONObject>> entries() {
        if (type != 'M')
            throw new JSONException("Error: entry iterator not available for JSONObject of type '" + type + "'");
        Iterator<Map.Entry<String, JSONObject>> it = new Iterator<Map.Entry<String, JSONObject>>() {
            static int index = 0;

            @Override
            public boolean hasNext() {
                return index < names.size();
            }

            @Override
            public Map.Entry<String, JSONObject> next() {
                return new Map.Entry<String, JSONObject>() {
                    int i = index++;

                    @Override
                    public String getKey() {
                        return names.get(i);
                    }

                    @Override
                    public JSONObject setValue(JSONObject obj) {
                        JSONObject ret = values.get(i);
                        values.set(i, obj);
                        return ret;
                    }

                    @Override
                    public JSONObject getValue() {
                        return values.get(i);
                    }
                };
            }
        };

        return it;
    }

    /**
     * Creates the JSONObjects string notation
     * 
     * @return the string notation
     * @throws JSONException if it encounters an invalid JSONObject type
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        switch (type) {
            case 'I':
                sb.append((Integer) value);
                break;
            case 'D':
                sb.append((Double) value);
                break;
            case 'S':
                sb.append('"');
                sb.append((String) value);
                sb.append('"');
                break;
            case 'B':
                sb.append((Boolean) value);
                break;
            case 'N':
                sb.append("null");
                break;
            case 'A':
                sb.append('[');
                for (JSONObject obj : values) {
                    sb.append(obj.toString());
                    sb.append(',');
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                sb.append(']');
                break;
            case 'M':
                sb.append('{');
                for (int i = 0; i < names.size(); i++) {
                    sb.append('"');
                    sb.append(names.get(i));
                    sb.append("\":");
                    sb.append(values.get(i).toString());
                    sb.append(',');
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                sb.append('}');
                break;
            default:
                throw new JSONException("Error: invalid JSONObjecttype '" + type + "' encountered");
        }
        return sb.toString();
    }

    /**
     * @param indentCount the initial indent count, should be 0
     * @return an indented and formatted JSON string
     */
    public String toBeautifulString(int indentCount) {
        StringBuilder sb = new StringBuilder("");
        switch (type) {
            case 'I':
                sb.append((Integer) value);
                break;
            case 'D':
                sb.append((Double) value);
                break;
            case 'S':
                sb.append('"');
                sb.append((String) value);
                sb.append('"');
                break;
            case 'B':
                sb.append((Boolean) value);
                break;
            case 'N':
                sb.append("null");
                break;
            case 'A':
                sb.append("[\n");
                for (JSONObject obj : values) {
                    for (int i = 0; i < indentCount + 1; i++)
                        sb.append(indent);
                    sb.append(obj.toBeautifulString(indentCount + 1));
                    sb.append(",\n");
                }
                if (sb.length() > 2)
                    sb.deleteCharAt(sb.length() - 2);
                for (int j = 0; j < indentCount; j++)
                    sb.append(indent);
                sb.append(']');
                break;
            case 'M':
                sb.append("{\n");
                for (int i = 0; i < names.size(); i++) {
                    for (int j = 0; j < indentCount + 1; j++)
                        sb.append(indent);
                    sb.append('"');
                    sb.append(names.get(i));
                    sb.append("\": ");
                    sb.append(values.get(i).toBeautifulString(indentCount + 1));
                    sb.append(",\n");
                }
                if (sb.length() > 2)
                    sb.deleteCharAt(sb.length() - 2);
                for (int j = 0; j < indentCount; j++)
                    sb.append(indent);
                sb.append('}');
                break;
            default:
                throw new JSONException("Error: invalid JSONObjecttype '" + type + "' encountered");
        }
        return sb.toString();

    }

    /**
     * Adds a value to the JSONObject, meant to be used on objects of type 'A'
     * special case when used on objects of type 'M', it will set the last
     * name\value pairs value, this is used in {@link JSONObject#parseString(String)
     * parseString}
     * 
     * @param value the value to add to the JSONObject
     * @throws JSONException if the JSONObject is not of type 'A', 'M'
     */
    public void add(JSONObject value) {
        if (type != 'A' && type != 'M' && type != 'U')
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

    /**
     * @param i the integer to add to the JSONObject
     * @see JSONObject#add(JSONObject)
     */
    public void add(int i) {
        add(new JSONObject(i));
    }

    /**
     * @param d the double to add to the JSONObject
     * @see JSONObject#add(JSONObject)
     */
    public void add(double d) {
        add(new JSONObject(d));
    }

    /**
     * @param s the String to add to the JSONObject
     * @see JSONObject#add(JSONObject)
     */
    public void add(String s) {
        add(new JSONObject(s));
    }

    /**
     * @param b the boolean to add to the JSONObject
     * @see JSONObject#add(JSONObject)
     */
    public void add(boolean b) {
        add(new JSONObject(b));
    }

    /**
     * adds a null value to the JSONObject
     * 
     * @see JSONObject#add(JSONObject)
     */
    public void addNull() {
        add(nullObject());
    }

    /**
     * @param name  the name of the name/value pair
     * @param value the value of the name/value pair
     * @throws JSONException if the JSONObject is not of type 'M'
     */
    public void add(String name, JSONObject value) {
        if (type != 'M' && type != 'U')
            throw new JSONException("Error: JSONObject is not of type map...");

        if (type == 'U')
            type = 'M';
        if (names == null)
            names = new ArrayList<String>();
        if (values == null)
            values = new ArrayList<JSONObject>();
        names.add(name);
        values.add(value);
    }

    /**
     * @param name the name of the name/value pair
     * @param i    the integer value of the name/value pair
     * @see JSONObject#add(String, JSONObject)
     */
    public void add(String name, int i) {
        add(name, new JSONObject(i));
    }

    /**
     * @param name the name of the name/value pair
     * @param d    the double value of the name/value pair
     * @see JSONObject#add(String, JSONObject)
     */
    public void add(String name, double d) {
        add(name, new JSONObject(d));
    }

    /**
     * @param name the name of the name/value pair
     * @param s    the String value of the name/value pair
     * @see JSONObject#add(String, JSONObject)
     */
    public void add(String name, String s) {
        add(name, new JSONObject(s));
    }

    /**
     * @param name the name of the name/value pair
     * @param b    the boolean value of the name/value pair
     * @see JSONObject#add(String, JSONObject)
     */
    public void add(String name, boolean b) {
        add(name, new JSONObject(b));
    }

    /**
     * Adds a name/value pair with null value to the JSONObject
     * 
     * @param name the name of the name/value pair
     * @see JSONObject#add(String, JSONObject)
     */
    public void addNull(String name) {
        JSONObject obj = new JSONObject();
        obj.setNull();
        add(name, obj);
    }

    /**
     * @param name the name whose value to return
     * @return the value associated with the provided name
     * @throws JSONException if the JSONObject is not of type 'M'
     */
    public JSONObject get(String name) {
        if (type != 'M')
            throw new JSONException("Error: JSONObject is not of type map...");
        int index = names.indexOf(name);
        if (index == -1)
            return null;
        return values.get(index);
    }

    /**
     * @param index the index of the JSONObject to return
     * @return the value at the provided index
     * @throws JSONException if the JSONObject is not of type 'A'
     */
    public JSONObject get(int index) {
        if (type != 'A')
            throw new JSONException("Error: JSONObject is not of type map...");
        return values.get(index);
    }

    public static void main(String[] args) {
        String str = new String("""
                   [{\"a\":[1, 2], \"abc\"
                   :   12},    \"test\", \"test\", 12,
                null, 1123, false]""");
        JSONObject obj = JSONObject.parseString(str);
        System.out.println(obj.toBeautifulString(0));
        System.out.println("----------------------------");
        System.out.println(obj);
        System.out.println(obj.get(0).get("a").get(0));
        System.out.println(obj.get(0).get("a").get(1));
        System.out.println(obj.get(0).get("abc"));
        System.out.println(obj.get(1));
    }
}
