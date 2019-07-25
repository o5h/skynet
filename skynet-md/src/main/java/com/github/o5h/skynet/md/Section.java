package com.github.o5h.skynet.md;

import java.util.ArrayList;
import java.util.List;

public class Section {

    public enum Type {
        TEXT,
        LIST,
        CODE,
        TABLE,
        HTML
    }

    private final Type type;

    private final List<String> list = new ArrayList<String>();

    public Section(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void addLine(String line) {
        list.add(line);
    }

    public String[] getLines() {
        return list.toArray(new String[]{});
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }
}
