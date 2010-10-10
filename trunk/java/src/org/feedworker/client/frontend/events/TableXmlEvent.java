package org.feedworker.client.frontend.events;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * 
 * @author luca
 */
public class TableXmlEvent extends EventObject {

	private ArrayList<String[]> obj;

	public TableXmlEvent(Object source, ArrayList<String[]> obj) {
		super(source);
		this.obj = obj;
	}

	public ArrayList<String[]> getObj() {
		return obj;
	}
}
