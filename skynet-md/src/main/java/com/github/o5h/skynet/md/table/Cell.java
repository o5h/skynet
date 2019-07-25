package com.github.o5h.skynet.md.table;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private int colspan = 1;
    private List<Object> data = new ArrayList<Object>();

    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    public void addText(String text) {
        if (!data.isEmpty()) {
            Object object = data.get(data.size() - 1);
            if (object instanceof String) {
                data.set(data.size() - 1, object + text);
            }
        } else {
            data.add(text);
        }
    }

    public void addInput(Input input) {
        this.data.add(input);
    }

    public Object[] getData() {
        return this.data.toArray(new Object[]{});
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (colspan != 1) {
            sb.append("colspan=").append(colspan);
        }
        for (Object o : data) {
            sb.append(o.toString());
        }
        return sb.toString();

    }

}
