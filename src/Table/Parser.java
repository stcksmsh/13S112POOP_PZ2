package Table;

import java.io.IOException;

abstract public class Parser {
    abstract public int save(Table table, String filename);

    abstract public Table open(String filename);

}
