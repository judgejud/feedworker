package org.feedworker.client.frontend.events;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * 
 * @author luca
 */
public class TableRssEvent extends EventObject {
	private ArrayList<Object[]> objs;
	private String nameTableDest;

	public TableRssEvent(Object source, ArrayList<Object[]> _data, String _name) {
		super(source);
		objs = _data;
		nameTableDest = _name;
	}

	public ArrayList<Object[]> getObjs() {
		return objs;
	}

	public String getNameTableDest() {
		return nameTableDest;
	}
}