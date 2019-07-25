package com.github.o5h.skynet.md.table;

import java.util.ArrayList;

public class Row {

	private ArrayList<Cell> cells = new ArrayList<Cell>();

	public void add(Cell cell) {
		cells.add(cell);
	}

	public Cell[] getCells() {
		return cells.toArray(new Cell[]{});
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Cell c : cells) {
			sb.append(c.toString());
			sb.append("\t");
		}
		sb.append("\n");
		return sb.toString();
	}
}
