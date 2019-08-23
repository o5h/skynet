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

    public void writeUInt32(int value) throws IOException {
        this.os.writeInt(value);
    }

    public void writeUInt64(long value) throws IOException {
        this.os.writeLong(value);
    }

    public void writeInt32(int value) throws IOException {
        this.os.writeInt(value);
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

    public void writeVector3d(double[] v) throws IOException {
        this.os.writeDouble(v[0]);
        this.os.writeDouble(v[1]);
        this.os.writeDouble(v[2]);
    }

    public void writeVector6d(double[] v) throws IOException {
        this.os.writeDouble(v[0]);
        this.os.writeDouble(v[1]);
        this.os.writeDouble(v[2]);
        this.os.writeDouble(v[3]);
        this.os.writeDouble(v[4]);
        this.os.writeDouble(v[5]);
    }

    public void writeVector6i(int[] v) throws IOException {
        this.os.writeInt(v[0]);
        this.os.writeInt(v[1]);
        this.os.writeInt(v[2]);
        this.os.writeInt(v[3]);
        this.os.writeInt(v[4]);
        this.os.writeInt(v[5]);
    }

    public void writeVector6u(int[] v) throws IOException {
        this.os.writeInt(v[0]);
        this.os.writeInt(v[1]);
        this.os.writeInt(v[2]);
        this.os.writeInt(v[3]);
        this.os.writeInt(v[4]);
        this.os.writeInt(v[5]);
    }

    @Override
    public void close() throws IOException {
        this.os.close();
    }
}