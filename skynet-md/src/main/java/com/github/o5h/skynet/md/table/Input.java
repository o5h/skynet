package com.github.o5h.skynet.md.table;

public class Input {

    private final String id;
    private final String type;

    public Input(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "[" + id + ":" + type + "]";
    }
}
