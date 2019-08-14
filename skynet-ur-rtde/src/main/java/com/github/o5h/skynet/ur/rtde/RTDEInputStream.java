package com.github.o5h.skynet.ur.rtde;

import java.io.*;

public class RTDEInputStream implements Closeable {

    private final DataInputStream is;

    public RTDEInputStream(InputStream is) {
        this.is = new DataInputStream(is);
    }

    public boolean readBool() throws IOException {
        return is.readUnsignedByte() != 0;
    }

    public int readUInt8() throws IOException {
        return is.readUnsignedByte();
    }

    public int readUInt16() throws IOException {
        return is.readUnsignedShort();
    }

    public int readInt32() throws IOException {
        return is.readInt();
    }

    public final long readUInt32() throws IOException {
        long ch1 = is.read();
        long ch2 = is.read();
        long ch3 = is.read();
        long ch4 = is.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    public void readFully(byte[] data) throws IOException {
        this.is.readFully(data);
    }

    @Override
    public void close() throws IOException {
        is.close();
    }
}
