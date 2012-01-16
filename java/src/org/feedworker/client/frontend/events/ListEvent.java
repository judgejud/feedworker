package org.feedworker.client.frontend.events;

import java.util.ArrayList;
import java.util.EventObject;
/**
 *
 * @author Luca
 */
public class ListEvent extends EventObject{
    private Object[][] matrix;
    private ArrayList<Object[]> arraylist;
    private Object[] array;
    private String name, oper, nick;

    public ListEvent(Object source, String name, Object[][] array) {
        super(source);
        this.name = name;
        this.matrix = array;
    }
    
    public ListEvent(Object source, String name, ArrayList<Object[]> array) {
        super(source);
        this.name = name;
        this.arraylist = array;
    }
    
    public ListEvent(Object source, String name, String oper, String nick) {
        super(source);
        this.name = name;
        this.oper = oper;
        this.nick = nick;
    }
    
    public ListEvent(Object source, String name, String oper, Object[] nicks) {
        super(source);
        this.name = name;
        this.oper = oper;
        this.array = nicks;
    }

    public String getName() {
        return name;
    }

    public String getOper() {
        return oper;
    }

    public String getNick() {
        return nick;
    }
    
    public Object[][] getMatrix() {
        return matrix;
    }
    
    public ArrayList<Object[]> getArrayList() {
        return arraylist;
    }

    public Object[] getArray() {
        return array;
    }
}