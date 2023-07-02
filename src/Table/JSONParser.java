package Table;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Iterator;
import JSOpeN.JSONObject;

public class JSONParser extends Parser {
    @Override
    public int save(Table table, String filename) {
        try {
            FileWriter file = new FileWriter(filename);
            String currentSheetName = null;
            JSONObject json = new JSONObject();
            JSONObject tableJSON = new JSONObject();
            json.add("table", tableJSON);
            JSONObject sheetJSON = null;
            for (AbstractMap.SimpleEntry<String, Cell> entry : table) {
                Cell cell = entry.getValue();
                String sheetName = entry.getKey();
                String column = cell.getCellIdentifier().getColumn();
                int row = cell.getCellIdentifier().getRow();
                String value = cell.getValue();
                String formatCode = cell.getFormatCode();
                if (value.equals("") && formatCode.equals("T")) {
                    continue;
                }
                JSONObject cellJSON = new JSONObject();
                cellJSON.add("row", row);
                cellJSON.add("column", column);
                cellJSON.add("value", value);
                cellJSON.add("formatCode", formatCode);
                if (currentSheetName == null || !currentSheetName.equals(sheetName)) {
                    currentSheetName = sheetName;
                    sheetJSON = new JSONObject();
                    tableJSON.add(sheetName, sheetJSON);
                }
                sheetJSON.add(cellJSON);
            }
            file.write(json.toString());
            file.close();
        } catch (IOException IOe) {
            return -1;
        }
        return 0;
    }

    @Override
    public Table open(String filename) {
        Table table = new Table();
        try {
            String data = Files.readString(Path.of(filename));
            JSONObject obj = JSONObject.parseString(data).get("table");
            Iterator<String> names = obj.names();
            while (names.hasNext()) {
                String sheetName = names.next();
                Iterator<JSONObject> cells = obj.get(sheetName).values();
                while (cells.hasNext()) {
                    JSONObject cell = cells.next();
                    String column = (String) cell.get("column").getValue();
                    int row = ((Integer) cell.get("row").getValue()).intValue();
                    String value = (String) cell.get("value").getValue();
                    String formatCode = (String) cell.get("formatCode").getValue();
                    table.setValue(sheetName, column, row, value, formatCode);
                }
            }
            table.init();
        } catch (IOException ioe) {
            return null;
        }

        return table;
    }
}
