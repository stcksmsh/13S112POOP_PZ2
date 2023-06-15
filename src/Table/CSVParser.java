package Table;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Scanner;

public class CSVParser extends Parser {

    private void readLineIntoTable(Table table, String line) {
        String strings[] = line.split("\\s*,\\s*");
        String sheetName = strings[0];
        String colum = strings[1];
        int row = Integer.parseInt(strings[2]);
        String value = strings[3];
        String FormatCode = strings[4];
        table.setValue(sheetName, colum, row, value, FormatCode);
    }

    @Override
    public Table open(String filename) {
        Table table = new Table();

        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                readLineIntoTable(table, line);
            }
            scanner.close();
            table.init();
        } catch (FileNotFoundException fnfe) {
            return null;
        }
        return table;
    }

    @Override
    public int save(Table table, String filename) {
        try {
            FileWriter file = new FileWriter(filename);
            BufferedWriter bw = new BufferedWriter(file);

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
                bw.write(String.format("%s,%s,%d,%s,%s\n", sheetName, column, row, value, formatCode));
            }
            bw.close();
            file.close();
        } catch (IOException IOe) {
            return -1;
        }
        return 0;
    }
}
