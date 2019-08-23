package com.github.o5h.skynet.ur.rtde;

import java.io.*;

public class RTDEInputStream implements Closeable {

    private final DataInputStream is;

    public RTDEInputStream(InputStream is) {
        this.is = new DataInputStream(is);
    }

    public int available() throws IOException {
        return is.available();
    }

    public boolean readBool() throws IOException {
        return is.readUnsignedByte() != 0;
    }

    public byte readUInt8() throws IOException {
        return (byte)is.readUnsignedByte();
    }

    public short readUInt16() throws IOException {
        return (short)is.readUnsignedShort();
    }

    public int readInt32() throws IOException {
        return is.readInt();
    }

    public int readUInt32() throws IOException {
        return is.readInt();
    }

    public long readUInt64() throws IOException {
        return is.readLong();
    }

    public double readDouble() throws IOException {
        return is.readDouble();
    }

    public void readFully(byte[] data) throws IOException {
        this.is.readFully(data);
    }

    public double[] readVector6Double() throws IOException {
        double[] vec = new double[6];
        vec[0] = readDouble();
        vec[1] = readDouble();
        vec[2] = readDouble();
        vec[3] = readDouble();
        vec[4] = readDouble();
        vec[5] = readDouble();
        return vec;
    }

    public double[] readVector3Double() throws IOException {
        double[] vec = new double[3];
        vec[0] = readDouble();
        vec[1] = readDouble();
        vec[2] = readDouble();
        return vec;
    }

    public int[] readVector6Int32() throws IOException {
        int[] vec = new int[6];
        vec[0] = readInt32();
        vec[1] = readInt32();
        vec[2] = readInt32();
        vec[3] = readInt32();
        vec[4] = readInt32();
        vec[5] = readInt32();
        return vec;
    }

    public long[] readVector6UInt32() throws IOException {
        long[] vec = new long[6];
        vec[0] = readUInt32();
        vec[1] = readUInt32();
        vec[2] = readUInt32();
        vec[3] = readUInt32();
        vec[4] = readUInt32();
        vec[5] = readUInt32();
        return vec;
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

}