package com.github.o5h.skynet.md.table;

import java.util.ArrayList;

public class Table {

	private final ArrayList<Row> rows = new ArrayList<Row>();

	public void add(Row row) {
		rows.add(row);
	}

	public Row[] getRows() {
		return rows.toArray(new Row[]{});
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Row r : rows) {
			sb.append(r.toString());
		}
		return sb.toString();
	}
}
