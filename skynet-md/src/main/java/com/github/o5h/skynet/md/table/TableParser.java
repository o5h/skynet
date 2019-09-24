package com.github.o5h.skynet.md.table;

import java.util.StringTokenizer;

public class TableParser {

    public Table parse(String[] lines) {
        Table table = new Table();
        for (String line : lines) {
            Row row = parseRow(line);
            table.add(row);
        }
        return table;
    }

    private Row parseRow(String l) {
        Row row = new Row();
        StringTokenizer st = new StringTokenizer(l, "|");
        while (st.hasMoreElements()) {
            String text = st.nextToken();
            text = text.trim();
            Cell cell = parseCell(text);
            row.add(cell);
        }
        return row;
    }

    private Cell parseCell(String text) {
        Cell cell = new Cell();
        cell.addText(text);
        return cell;
    }

}
