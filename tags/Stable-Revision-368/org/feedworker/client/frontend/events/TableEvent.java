package org.feedworker.client.frontend.events;

import java.util.ArrayList;
import java.util.EventObject;
import org.feedworker.object.Subtitle;

/**
 * 
 * @author luca
 */
public class TableEvent extends EventObject {

    private ArrayList<Object[]> array;
    private ArrayList<Subtitle> array2;
    private String nameTableDest;

    public TableEvent(Object source, ArrayList<Object[]> _data, String _name) {
        super(source);
        array = _data;
        nameTableDest = _name;
    }
    
    public TableEvent(Object source, String _name, ArrayList<Subtitle> _data) {
        super(source);
        array2 = _data;
        nameTableDest = _name;
    }

    public ArrayList<Object[]> getArray() {
        return array;
    }
    
    public ArrayList<Subtitle> getArraySubtitle() {
        return array2;
    }

    public String getNameTableDest() {
        return nameTableDest;
    }
}