package org.feedworker.client.frontend.events;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * 
 * @author luca
 */
public class TableEvent extends EventObject {

    private ArrayList<Object[]> array;
    private String nameTableDest;
    private boolean addRows;

    public TableEvent(Object source, ArrayList<Object[]> _data, String _name) {
        super(source);
        array = _data;
        nameTableDest = _name;
        addRows = true;
    }
    
    public TableEvent(Object source, ArrayList<Object[]> _data, String _name, boolean _addRows) {
        super(source);
        array = _data;
        nameTableDest = _name;
        addRows = _addRows;
    }

    public ArrayList<Object[]> getArray() {
        return array;
    }

    public String getNameTableDest() {
        return nameTableDest;
    }
    
    public boolean isAddRows(){
        return addRows;
    }
}