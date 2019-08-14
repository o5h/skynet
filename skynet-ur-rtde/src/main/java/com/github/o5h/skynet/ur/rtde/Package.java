package com.github.o5h.skynet.ur.rtde;

import java.io.IOException;

public abstract class Package {

    public static final int RTDE_REQUEST_PROTOCOL_VERSION = 86; //V
    public static final int RTDE_GET_URCONTROL_VERSION = 118;//	v


    protected final int type; //uint8_t

    public Package(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public abstract int getSize();

    public abstract void writeTo(RTDEOutputStream os) throws IOException;

    public abstract void readFrom(RTDEInputStream is) throws IOException;

    protected void writeHeader(RTDEOutputStream os) throws IOException {
        os.writeUInt16(getSize());
        os.writeUInt8(type);
    }

    @Override
    public String toString() {
        return "{" + type + "}";
    }
}
