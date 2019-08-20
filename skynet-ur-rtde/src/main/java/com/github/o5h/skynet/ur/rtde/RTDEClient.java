package com.github.o5h.skynet.ur.rtde;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * See User manual for details.
 */
public class RTDEClient {

    public static final int PORT = 30004;

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final Logger LOG = LoggerFactory.getLogger(RTDEClient.class);

    private volatile Socket socket;
    private RTDEOutputStream outputStream;
    private RTDEInputStream inputStream;
    private volatile int protocolVersion = 1;
    private volatile int requestedProtocolVersion = 0;
    private final RTDEOutputHandler handler;
    private RTDEOutputParam[] outParameters;
    private boolean[] supported;

    public RTDEClient(RTDEOutputHandler handler) {
        this.handler = handler;
    }

    public synchronized boolean connect(String host, int port, int timeout) {
        LOG.debug("Connect to RTDE {} {}", host, port);
        if (this.socket != null) {
            return this.socket.isConnected();
        }
        try {
            this.socket = new Socket();
            this.socket.setKeepAlive(true);
            this.socket.setSoTimeout(timeout);
            this.socket.connect(new InetSocketAddress(host, port));
            this.outputStream = new RTDEOutputStream(new DataOutputStream(socket.getOutputStream()));
            this.inputStream = new RTDEInputStream(new DataInputStream(socket.getInputStream()));
            LOG.debug("Connected to RTDE {}:{}", host, port);
            this.handler.onConnect();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isConnected()) {
                        while (available()) {
                            receive();
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            disconnect();
                        }
                    }
                }
            });
            thread.start();
            return true;
        } catch (IOException e) {
            LOG.error("Can't connect to RTDE.", e);
            disconnect();
            return false;
        }
    }

    private synchronized void receive() {
        if (socket == null) {
            LOG.error("Can't read. Connection is closed");
            return;
        }
        try {
            int size = this.inputStream.readUInt16();
            int type = this.inputStream.readUInt8();
            switch (type) {
                case RTDEProtocol.RTDE_REQUEST_PROTOCOL_VERSION:
                    onProtocolVersion();
                    break;
                case RTDEProtocol.RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS:
                    onControlPackageOutputResponse(size);
                    break;
                case RTDEProtocol.RTDE_CONTROL_PACKAGE_START:
                    onStart();
                    break;
                case RTDEProtocol.RTDE_CONTROL_PACKAGE_PAUSE:
                    onPause();
                    break;
                case RTDEProtocol.RTDE_DATA_PACKAGE:
                    onDataPackage(size);
                    break;
                default:
                    onUnsupportedPackage(size, type);
                    break;
            }
        } catch (IOException e) {
            LOG.error("Unexpected exception", e);
            disconnect();
        }
    }

    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected();
    }

    public synchronized boolean disconnect() {
        if (socket == null) {
            return true;
        }
        this.handler.onDisconnect();
        ;
        closeSilently(socket);
        this.socket = null;
        closeSilently(this.outputStream);
        this.outputStream = null;
        closeSilently(this.inputStream);
        this.inputStream = null;
        LOG.debug("Disconnected");
        return true;
    }

    public void requestProtocolVersion(int protocolVersion) throws IOException {
        this.requestedProtocolVersion = protocolVersion;
        this.outputStream.writeUInt16(5);
        this.outputStream.writeUInt8(RTDEProtocol.RTDE_REQUEST_PROTOCOL_VERSION);
        this.outputStream.writeUInt16(protocolVersion);
        this.outputStream.flush();
    }

    public synchronized void setupOutputsV1(RTDEOutputParam... variables) throws IOException {
        this.outParameters = new RTDEOutputParam[variables.length];
        this.supported = new boolean[variables.length];

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < variables.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            RTDEOutputParam output = variables[i];
            this.outParameters[i] = output;
            sb.append(output.name);
        }
        String variableNames = sb.toString();
        byte[] data = variableNames.getBytes(DEFAULT_CHARSET);

        this.outputStream.writeUInt16(3 + data.length);
        this.outputStream.writeUInt8(RTDEProtocol.RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS);
        this.outputStream.write(data);
        this.outputStream.flush();
    }

    public synchronized void setupOutputsV2(double outputFrequency, RTDEOutputParam... variables) throws IOException {
        this.outParameters = new RTDEOutputParam[variables.length];
        this.supported = new boolean[variables.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < variables.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            RTDEOutputParam output = variables[i];
            this.outParameters[i] = output;
            sb.append(output.name);
        }
        String variableNames = sb.toString();
        byte[] data = variableNames.getBytes(DEFAULT_CHARSET);

        this.outputStream.writeUInt16(11 + data.length);
        this.outputStream.writeUInt8(RTDEProtocol.RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS);
        this.outputStream.writeDouble(outputFrequency);
        this.outputStream.write(data);
        this.outputStream.flush();
    }

    public synchronized void start() throws IOException {
        this.outputStream.writeUInt16(3);
        this.outputStream.writeUInt8(RTDEProtocol.RTDE_CONTROL_PACKAGE_START);
        this.outputStream.flush();
    }

    public synchronized void pause() throws IOException {
        this.outputStream.writeUInt16(3);
        this.outputStream.writeUInt8(RTDEProtocol.RTDE_CONTROL_PACKAGE_PAUSE);
        this.outputStream.flush();
    }

    private void onProtocolVersion() throws IOException {
        if (this.inputStream.readBool()) {
            this.protocolVersion = requestedProtocolVersion;
            this.handler.onProtocolVersion(this.protocolVersion);
        }
    }

    private void onStart() throws IOException {
        if (this.inputStream.readBool()) {
            this.handler.onControlPackageStart();
        }
    }

    private void onPause() throws IOException {
        if (this.inputStream.readBool()) {
            this.handler.onControlPackagePause();
        }
    }

    private void onControlPackageOutputResponse(int size) throws IOException {
        if (this.protocolVersion == 1) {
            int stringSize = size - 3;
            recepyOutputResponse(stringSize);
        } else if (this.protocolVersion == 2) {
            this.inputStream.readUInt8(); // ignored
            int stringSize = size - 4;
            recepyOutputResponse(stringSize);
        } else {
            throw new IOException(" Unsupported protocol " + protocolVersion);
        }
    }

    private void recepyOutputResponse(int stringSize) throws IOException {
        byte[] data = new byte[stringSize];
        this.inputStream.readFully(data);
        String values = new String(data, DEFAULT_CHARSET);
        String[] types = values.split(",");
        ArrayList<RTDEOutputParam> supportedParams = new ArrayList<RTDEOutputParam>();
        ArrayList<RTDEOutputParam> unsupportedParams = new ArrayList<RTDEOutputParam>();
        for (int i = 0; i < types.length; i++) {
            String typeName = types[i];
            RTDEDataType type = RTDEDataType.valueOf(typeName);

            if (this.outParameters[i].type == type) {
                supportedParams.add(this.outParameters[i]);
            } else {
                unsupportedParams.add(this.outParameters[i]);
            }
        }
        handler.onSetupOutputsResponse(
                supportedParams.toArray(new RTDEOutputParam[0]),
                unsupportedParams.toArray(new RTDEOutputParam[0])
        );
        LOG.debug("VALUES {}", values);
    }

    private void onDataPackage(int size) throws IOException {
        if (protocolVersion == 2) {
            this.inputStream.readUInt8(); // ignored
        }

        for (int i = 0; i < this.outParameters.length; i++) {
            RTDEOutputParam var = this.outParameters[i];
            switch (this.outParameters[i].type) {
                case VECTOR6D:
                    this.handler.onData(var, this.inputStream.readVector6Double());
                    break;
                case VECTOR3D:
                    this.handler.onData(var, this.inputStream.readVector3Double());
                    break;
                case VECTOR6INT32:
                    this.handler.onData(var, this.inputStream.readVector6Int32());
                    break;
                case VECTOR6UINT32:
                    this.handler.onData(var, this.inputStream.readVector6UInt32());
                    break;
                case DOUBLE:
                    this.handler.onData(var, this.inputStream.readDouble());
                    break;
                case UINT64:
                    this.handler.onData(var, this.inputStream.readUInt64());
                    break;
                case UINT32:
                    this.handler.onData(var, this.inputStream.readUInt32());
                    break;
                case INT32:
                    this.handler.onData(var, this.inputStream.readInt32());
                    break;
                case BOOL:
                    this.handler.onData(var, this.inputStream.readUInt8() != 0);
                    break;
                case UINT8:
                    this.handler.onData(var, this.inputStream.readUInt8());
                    break;
            }
        }
    }

    private void onUnsupportedPackage(int size, int type) throws IOException {
        byte[] data = new byte[size - 3];
        this.inputStream.readFully(data);
        LOG.warn("Unsupported package type {} {} {}", type, size, new String(data, DEFAULT_CHARSET));
    }

    private synchronized boolean available() {
        try {
            if (!isConnected()) {
                return false;
            }
            return this.inputStream.available() >= 3;
        } catch (IOException e) {
            LOG.error("Unexpected exception", e);
            disconnect();
            return false;
        }
    }

    private static void closeSilently(Closeable closeable) {
        try {
            if (closeable == null) {
                return;
            }
            closeable.close();
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

}
