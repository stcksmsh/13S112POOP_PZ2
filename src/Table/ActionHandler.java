package Table;

import java.util.ArrayList;

public class ActionHandler extends ArrayList<Action> {
    private Table table;
    private int index = -1;

    public ActionHandler(Table table) {
        super();
        this.table = table;
    }

    public boolean add(String sheetName, Cell cell, String oldValue, String newValue) {
        if (index >= 0)
            removeRange(index, size() - 1); /// clear all "future" actions
        index++; /// increment current action position
        Action previousAction = null;
        if (size() > 0)
            previousAction = get(size() - 1);
        ;
        Action newAction = new Action(sheetName, oldValue, newValue, cell.getCellIdentifier().getColumn(),
                cell.getCellIdentifier().getRow(), cell.getFormatCode());
        if (previousAction == null || !newAction.equals(previousAction))
            return super.add(new Action(sheetName, oldValue, newValue, cell.getCellIdentifier().getColumn(),
                    cell.getCellIdentifier().getRow(), cell.getFormatCode())); /// add the new action
        return false;
    }

    public void undo() {
        if (index == -1)
            return;
        get(index).undo(table);
        index--;
    }

    public void redo() {
        if (index >= size() - 1)
            return;
        index++;
        get(index).redo(table);
    }

}
