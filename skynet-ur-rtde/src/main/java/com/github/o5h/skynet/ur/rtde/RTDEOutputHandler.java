package com.github.o5h.skynet.ur.rtde;

public abstract class RTDEOutputHandler {

    public void onConnect() {
    }

    public void onDisconnect() {
    }

    public void onProtocolVersion(int protocolVersion) {
    }

    public void onSetupOutputsResponse(RTDEOutputParam[] supported,
                                       RTDEOutputParam[] unsupported) {
    }

    public void onControlPackageStart() {
    }

    public void onControlPackagePause() {
    }

    public abstract void onData(RTDEOutputParam output, Object value);
}
