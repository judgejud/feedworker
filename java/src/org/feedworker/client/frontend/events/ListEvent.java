package org.feedworker.client.frontend.events;

import java.util.ArrayList;
import java.util.EventObject;
/**
 *
 * @author Luca
 */
public class ListEvent extends EventObject{
    private Object[][] matrix;
    private ArrayList<Object[]> array;
    private String name, oper;

    public ListEvent(Object source, String name, Object[][] array) {
        super(source);
        this.name = name;
        this.matrix = array;
    }
    
    public ListEvent(Object source, String name, ArrayList<Object[]> array) {
        super(source);
        this.name = name;
        this.array = array;
    }

    public String getName() {
        return name;
    }

    public String getOper() {
        return oper;
    }
    
    public Object[][] getMatrix() {
        return matrix;
    }
    
    public ArrayList<Object[]> getArrayList() {
        return array;
    }
}