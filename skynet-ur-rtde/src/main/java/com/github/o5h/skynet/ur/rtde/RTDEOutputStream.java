package com.github.o5h.skynet.ur.rtde;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTDEOutputStream implements Closeable {
    private final DataOutputStream os;

    public RTDEOutputStream(DataOutputStream os) {
        this.os = os;
    }

    public void writeUInt8(int value) throws IOException {
        this.os.writeByte(value);
    }

    public void writeUInt16(int value) throws IOException {
        this.os.writeShort(value);
    }

    @Override
    public void close() throws IOException {
        this.os.close();
    }

    public void flush() throws IOException {
        this.os.flush();
    }
}
