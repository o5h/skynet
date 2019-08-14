package com.github.o5h.skynet.ur.rtde;

import java.io.IOException;

public class RequestProtocolVersionRequest extends Package {
    private int protocolVersion;

    public RequestProtocolVersionRequest() {
        super(RTDE_REQUEST_PROTOCOL_VERSION);
    }

    public RequestProtocolVersionRequest(int protocolVersion) {
        this();
        this.protocolVersion= protocolVersion;
    }

    @Override
    public int getType() {
        return super.getType();
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public int getSize() {
        return 5;
    }

    @Override
    public void writeTo(RTDEOutputStream os) throws IOException {
        writeHeader(os);
        os.writeUInt16(protocolVersion);
    }

    @Override
    public void readFrom(RTDEInputStream is) {

    }
}
