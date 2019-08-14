package com.github.o5h.skynet.ur.rtde;

import java.io.IOException;

public class RequestProtocolVersionResponse extends Package {
    private boolean success;

    public RequestProtocolVersionResponse() {
        super(RTDE_REQUEST_PROTOCOL_VERSION);
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public void writeTo(RTDEOutputStream os) throws IOException {

    }

    @Override
    public void readFrom(RTDEInputStream is) throws IOException {
        this.success = is.readBool();
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return String.valueOf(success);
    }
}
