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
    private RTDEInputParam[] requestedInput;
    private RTDEInputParam[][] inputParams = new RTDEInputParam[255][];


    public RTDEClient(RTDEOutputHandler handler) {
        this.handler = handler;
    }

    public synchronized boolean connect(String host, int port, int timeout) {
        LOG.debug("Connecting to RTDE {} {}", host, port);
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
                case RTDEProtocol.RTDE_CONTROL_PACKAGE_SETUP_INPUTS:
                    onControlPackageInputsResponse(size);
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

    public synchronized void setupOutputsV2(double outputFrequency, RTDEOutputParam... outputs) throws IOException {
        if (outputs.length == 0) {
            return;
        }
        this.outParameters = new RTDEOutputParam[outputs.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < outputs.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            RTDEOutputParam output = outputs[i];
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

    public synchronized void startOutput() throws IOException {
        this.outputStream.writeUInt16(3);
        this.outputStream.writeUInt8(RTDEProtocol.RTDE_CONTROL_PACKAGE_START);
        this.outputStream.flush();
    }

    public synchronized void pauseOutput() throws IOException {
        this.outputStream.writeUInt16(3);
        this.outputStream.writeUInt8(RTDEProtocol.RTDE_CONTROL_PACKAGE_PAUSE);
        this.outputStream.flush();
    }

    public void setupInputs(RTDEInputParam... inputs) throws IOException {
        this.requestedInput = new RTDEInputParam[inputs.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inputs.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            RTDEInputParam input = inputs[i];
            this.requestedInput[i] = input;
            sb.append(input.name);
        }
        String variableNames = sb.toString();
        byte[] data = variableNames.getBytes(DEFAULT_CHARSET);

        this.outputStream.writeUInt16(3 + data.length);
        this.outputStream.writeUInt8(RTDEProtocol.RTDE_CONTROL_PACKAGE_SETUP_INPUTS);
        this.outputStream.write(data);
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

    public void sendData(int id, Object... data) throws IOException {
        int packageSize = 0;
        RTDEInputParam[] params = this.inputParams[id];
        for (int i = 0; i < params.length; i++) {
            RTDEDataType type = params[i].type;
            if (type == RTDEDataType.STRING) {
                packageSize += ((String) data[i]).getBytes().length;
            } else {
                packageSize += params[i].type.size;
            }
        }

        this.outputStream.writeUInt16(packageSize + 4);
        this.outputStream.writeUInt8(RTDEProtocol.RTDE_DATA_PACKAGE);
        this.outputStream.writeUInt8(id);
        for (int i = 0; i < params.length; i++) {
            switch (params[i].type) {
                case BOOL:
                    if ((Boolean)data[i]) {
                        this.outputStream.writeUInt8(1);
                    } else {
                        this.outputStream.writeUInt8(0);
                    }
                    break;
                case UINT8:
                    this.outputStream.writeUInt8((Integer)data[i]);
                    break;
                case UINT32:
                    this.outputStream.writeUInt32((Integer)data[i]);
                    break;
                case UINT64:
                    this.outputStream.writeUInt64((Long)data[i]);
                    break;
                case INT32:
                    this.outputStream.writeInt32((Integer)data[i]);
                    break;
                case DOUBLE:
                    this.outputStream.writeDouble((Double)data[i]);
                    break;
                case VECTOR3D:
                    this.outputStream.writeVector3d((double[]) data[i]);
                    break;
                case VECTOR6D:
                    this.outputStream.writeVector6d((double[]) data[i]);
                    break;
                case VECTOR6INT32:
                    this.outputStream.writeVector6i((int[]) data[i]);
                    break;
                case VECTOR6UINT32:
                    this.outputStream.writeVector6u((int[]) data[i]);
                    break;
                case STRING:
                    this.outputStream.write(((String) data[i]).getBytes());
                    break;
            }
        }
        this.outputStream.flush();
    }


    private void onControlPackageInputsResponse(int size) throws IOException {
        int id = this.inputStream.readUInt8(); // ignored
        byte[] data = new byte[size - 4];
        this.inputStream.readFully(data);
        String values = new String(data, DEFAULT_CHARSET);
        String[] types = values.split(",");
        ArrayList<RTDEInputParam> supportedParams = new ArrayList<RTDEInputParam>();
        ArrayList<RTDEInputParam> unsupportedParams = new ArrayList<RTDEInputParam>();

        for (int i = 0; i < types.length; i++) {
            String typeName = types[i];
            RTDEDataType type = RTDEDataType.valueOf(typeName);
            if (this.requestedInput[i].type == type) {
                supportedParams.add(this.requestedInput[i]);
            } else {
                unsupportedParams.add(this.requestedInput[i]);
            }
        }
        if (id != 0) {
            inputParams[id] = supportedParams.toArray(new RTDEInputParam[]{});
            handler.onSetupInputsResponse(id, inputParams[id], null);
        } else {
            handler.onSetupInputsResponse(id, null, unsupportedParams.toArray(new RTDEInputParam[]{}));
        }
        LOG.debug("VALUES {}", values);
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
        LOG.warn("Unsupported package type {} size={} value={}", type, size, new String(data,  DEFAULT_CHARSET));
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
