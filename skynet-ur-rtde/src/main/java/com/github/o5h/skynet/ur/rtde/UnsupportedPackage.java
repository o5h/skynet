package com.github.o5h.skynet.ur.rtde;

import java.io.IOException;

public class UnsupportedPackage extends Package {
    private final int size;

    public UnsupportedPackage(int size, int type) {
        super(type);
        this.size = size;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public void writeTo(RTDEOutputStream os) throws IOException {

    }

    @Override
    public void readFrom(RTDEInputStream is) throws IOException {
        byte[] data = new byte[size - 3];
        is.readFully(data);
    }
}
