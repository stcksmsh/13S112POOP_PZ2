package JSOpeN;

public class JSONException extends RuntimeException {
    public JSONException(String errorMSG) {
        super(errorMSG);
    }

    public JSONException(int index, String string) {
        super("Error: unexpected character '" + string.charAt(index) + "' encountered at index "
                + Integer.toString(index) + " while parsing string:" + string);
    }
}
