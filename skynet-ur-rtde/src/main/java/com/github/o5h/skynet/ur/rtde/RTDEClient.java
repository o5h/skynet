package com.github.o5h.skynet.ur.rtde;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * See User manual for details.
 */
public class RTDEClient {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final int PORT = 30004;

    private enum VariableType {
        NOT_FOUND,
        VECTOR6D,
        VECTOR3D,
        VECTOR6INT32,
        VECTOR6UINT32,
        DOUBLE,
        UINT64,
        UINT32,
        INT32,
        BOOL,
        UINT8
    }

    public static final int RTDE_REQUEST_PROTOCOL_VERSION = 86; //V
    public static final int RTDE_GET_URCONTROL_VERSION = 118;//	v
    public static final int RTDE_TEXT_MESSAGE = 77;//	M
    public static final int RTDE_DATA_PACKAGE = 85;//	U
    public static final int RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS = 79;//	O
    public static final int RTDE_CONTROL_PACKAGE_START = 83;//	S

    private static final Logger LOG = LoggerFactory.getLogger(RTDEClient.class);

    public interface Handler {

        void onConnect();

        void onDisconnect();

        void onProtocolVersion(int protocolVersion);

        void onPackage(Package p);

        void onControlPackageStart();
    }

    private final Handler handler;
    private volatile Socket socket;
    private RTDEOutputStream outputStream;
    private RTDEInputStream inputStream;
    private volatile int protocolVersion = 1;
    private volatile int requestedProtocolVersion = 0;
    private Map<Integer, Recepy> recepies = new HashMap<Integer, Recepy>();

    public RTDEClient(Handler handler) {
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
                case RTDE_REQUEST_PROTOCOL_VERSION:
                    onProtocolVersion();
                    break;
                case RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS:
                    onControlPackageOutputResponse(size);
                    break;
                case RTDE_CONTROL_PACKAGE_START:
                    onStart();
                    break;
                case RTDE_DATA_PACKAGE:
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

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void requestProtocolVersion(int protocolVersion) throws IOException {
        this.outputStream.writeUInt16(5);
        this.outputStream.writeUInt8(RTDE_REQUEST_PROTOCOL_VERSION);
        this.outputStream.writeUInt16(protocolVersion);
        this.requestedProtocolVersion = 1;
    }

    public synchronized void setupOutputsV1(String... variables) throws IOException {
        StringBuilder sb = new StringBuilder();
        Recepy recepy = new Recepy();
        this.recepies.put(1, recepy);
        recepy.outVariableTypes = new VariableType[variables.length];
        recepy.outVariableNames = new String[variables.length];
        recepy.outVariableValue = new Object[variables.length];
        for (int i = 0; i < variables.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            String name = variables[i];
            recepy.outVariableNames[i] = name;
            sb.append(name);
        }
        String variableNames = sb.toString();
        byte[] data = variableNames.getBytes(DEFAULT_CHARSET);

        this.outputStream.writeUInt16(3 + data.length);
        this.outputStream.writeUInt8(RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS);
        this.outputStream.write(data);
        this.outputStream.flush();
    }

    public synchronized void start() throws IOException {
        this.outputStream.writeUInt16(3);
        this.outputStream.writeUInt8(RTDE_CONTROL_PACKAGE_START);
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

    private void onControlPackageOutputResponse(int size) throws IOException {
        if (this.protocolVersion == 1) {
            byte[] data = new byte[size - 3];
            this.inputStream.readFully(data);
            String values = new String(data, DEFAULT_CHARSET);
            String[] types = values.split(",");
            Recepy recepy = this.recepies.get(1);
            for (int i = 0; i < types.length; i++) {
                String typeName = types[i];
                VariableType type = VariableType.valueOf(typeName);
                recepy.outVariableTypes[i] = type;
            }
            LOG.debug("VALUES {}", values);
        } else if (this.protocolVersion == 2) {
            throw new UnsupportedOperationException("Not implemented yet");
        } else {
            throw new IOException(" Unsupported protocol " + protocolVersion);
        }
    }

    private void onDataPackage(int size) throws IOException {
        int recipeId = this.inputStream.readUInt8();
        Recepy recepy = this.recepies.get(recipeId);
        for (int i = 0; i < recepy.outVariableNames.length; i++) {
            switch (recepy.outVariableTypes[i]) {
                case NOT_FOUND:
                    break;
                case VECTOR6D:
                    Vector6d newValue = this.inputStream.readVector6d();
                    if (!newValue.equals(recepy.outVariableValue[i])) {
                        recepy.outVariableValue[i] = newValue;
                        LOG.debug("{}={}", recepy.outVariableNames[i], recepy.outVariableValue[i]);
                    }
                    break;
                case VECTOR3D:
                    break;
                case VECTOR6INT32:
                    break;
                case VECTOR6UINT32:
                    break;
                case DOUBLE:
                    break;
                case UINT64:
                    break;
                case UINT32:
                    break;
                case INT32:
                    break;
                case BOOL:
                    break;
                case UINT8:
                    break;
            }
        }
    }


    private void onUnsupportedPackage(int size, int type) throws IOException {
        byte[] data = new byte[size - 3];
        this.inputStream.readFully(data);
        LOG.warn("Unsupported package type {}", type);
    }

    private boolean available() {
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

    private class RTDEInputStream implements Closeable {

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

        private double readDouble() throws IOException {
            return is.readDouble();
        }

        public void readFully(byte[] data) throws IOException {
            this.is.readFully(data);
        }

        @Override
        public void close() throws IOException {
            is.close();
        }

        public Vector6d readVector6d() throws IOException {
            Vector6d vec = new Vector6d();
            vec.x = readDouble();
            vec.y = readDouble();
            vec.z = readDouble();
            vec.rx = readDouble();
            vec.ry = readDouble();
            vec.rz = readDouble();
            return vec;
        }

    }

    private class RTDEOutputStream implements Closeable {
        private final DataOutputStream os;

        public RTDEOutputStream(DataOutputStream os) {
            this.os = os;
        }

        public void writeUInt8(int value) throws IOException {
            this.os.writeByte(value);
        }

        public void writeUInt16(int value) throws IOException {
            this.os.writeShort(value);
        }

        @Override
        public void close() throws IOException {
            this.os.close();
        }

        public void flush() throws IOException {
            this.os.flush();
        }

        public void write(byte[] data) throws IOException {
            os.write(data);
        }
    }

    public static abstract class Package {

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

    private static class Recepy {
        VariableType[] outVariableTypes;
        String[] outVariableNames;
        Object[] outVariableValue;
    }

}
