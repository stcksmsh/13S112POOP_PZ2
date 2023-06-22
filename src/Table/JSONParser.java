package Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;

public class JSONParser extends Parser {
    @Override
    public int save(Table table, String filename) {
        try {
            FileWriter file = new FileWriter(filename);
            BufferedWriter bw = new BufferedWriter(file);
            String currentSheetName = null;
            bw.write("{\"table\":{");
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
                if (currentSheetName == null) {
                    currentSheetName = sheetName;
                    bw.write(String.format("\"%s\":[", currentSheetName));
                } else if (!currentSheetName.equals(sheetName)) {
                    bw.write(String.format("],\"%s\":[", currentSheetName));
                } else {
                    bw.write(",");
                }

                bw.write(String.format("{\"row\":\"%s\",\"column\":%d,\"value\":\"%s\",\"formatCode\":\"%s\"}", column,
                        row, value, formatCode));
            }
            bw.write("]}}");
            bw.close();
            file.close();
        } catch (IOException IOe) {
            return -1;
        }
        return 0;
    }

    @Override
    public Table open(String filename) {
        return null;
    }
}
