package com.github.o5h.skynet.ur.rtde;


import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RTDEOutputStream implements Closeable {
    private final DataOutputStream os;

    public RTDEOutputStream(OutputStream os) {
        this.os = new DataOutputStream(os);
    }

    public void writeUInt8(int value) throws IOException {
        this.os.writeByte(value);
    }

    public void writeUInt16(int value) throws IOException {
        this.os.writeShort(value);
    }

    public void flush() throws IOException {
        this.os.flush();
    }

    public void write(byte[] data) throws IOException {
        os.write(data);
    }

    public void writeDouble(double v) throws IOException {
        this.os.writeDouble(v);
    }

    @Override
    public void close() throws IOException {
        this.os.close();
    }
}