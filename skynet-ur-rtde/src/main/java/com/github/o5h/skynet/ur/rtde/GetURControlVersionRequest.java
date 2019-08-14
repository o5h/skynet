package com.github.o5h.skynet.ur.rtde;

import java.io.IOException;

public class GetURControlVersionRequest extends Package {

    public GetURControlVersionRequest() {
        super(RTDE_GET_URCONTROL_VERSION);
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public void writeTo(RTDEOutputStream os) throws IOException {

    }

    @Override
    public void readFrom(RTDEInputStream is) throws IOException {

    }
}
