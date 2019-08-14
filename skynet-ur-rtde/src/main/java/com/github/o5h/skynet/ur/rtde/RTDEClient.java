package com.github.o5h.skynet.ur.rtde;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * See User manual for details.
 */
public class RTDEClient {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final int PORT = 30004;

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
    private HashMap<String, Object> outputValueTypes = new HashMap<String, Object>();

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
            LOG.debug("Connected to RTDE {} {}", host, port);
            this.handler.onConnect();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isConnected()) {
                        while (available()) {
                            Package pkg = receive();
                            if (pkg == null) {
                                continue;
                            }
                            System.out.println(pkg);
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

    public void requestProtocolVersion(int i) {
        send(new RTDE_REQUEST_PROTOCOL_VERSION_REQUEST(1));
        this.requestedProtocolVersion = 1;
    }

    public void setupOutputsV1(String... variables) {
        StringBuilder sb = new StringBuilder();
        this.outputValueTypes.clear();
        for (int i = 0; i < variables.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            String name = variables[i];
            sb.append(name);
            this.outputValueTypes.put(name, null);
        }
        send(new RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS_V1(sb.toString()));
    }

    private boolean send(Package msg) {
        if (socket == null) {
            LOG.error("Can't send '{}'. Connection is closed", msg);
            return false;
        }
        try {
            msg.writeTo(this.outputStream);
            this.outputStream.flush();
            LOG.debug("Sent {}", msg);
        } catch (IOException e) {
            LOG.error("Unexpected exception", e);
            disconnect();
        }
        return true;
    }

    private synchronized Package receive() {
        if (socket == null) {
            LOG.error("Can't read. Connection is closed");
            return null;
        }
        try {
            int size = this.inputStream.readUInt16();
            int type = this.inputStream.readUInt8();
            LOG.debug("Receive {}", type);
            switch (type) {
                case RTDE_REQUEST_PROTOCOL_VERSION:
                    onRequestProtocolVersionResponse();
                    break;
                case RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS:
                    onControlPackageOutputResponse(size);
                    break;
                case RTDE_CONTROL_PACKAGE_START:
                    onControlPackageStart();
                    break;
                default:
                    Package pkg = onUnsupportedPackage(size, type);
                    return pkg;
            }
        } catch (IOException e) {
            LOG.error("Unexpected exception", e);
            disconnect();
            return null;
        }
        return null;
    }

    private void onControlPackageStart() throws IOException {
        RTDE_CONTROL_PACKAGE_START_RESPONSE response = new RTDE_CONTROL_PACKAGE_START_RESPONSE();
        response.readFrom(this.inputStream);
        if (response.isSuccess()) {
            this.handler.onControlPackageStart();
        }
    }


    private Package onUnsupportedPackage(int size, int type) throws IOException {
        Package pkg = new RTDEUnsupportedPackage(size, type);
        LOG.warn("Unsupported package type {}", type);
        pkg.readFrom(inputStream);
        LOG.debug("Received {}", pkg);
        return pkg;
    }

    private void onRequestProtocolVersionResponse() throws IOException {
        RTDE_REQUEST_PROTOCOL_VERSION_RESPONSE protocolVersion = new RTDE_REQUEST_PROTOCOL_VERSION_RESPONSE();
        protocolVersion.readFrom(inputStream);
        if (protocolVersion.isSuccess()) {
            this.protocolVersion = requestedProtocolVersion;
            this.handler.onProtocolVersion(this.protocolVersion);
        }
    }

    private void onControlPackageOutputResponse(int size) throws IOException {
        if (this.protocolVersion == 1) {
            RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS_RESPONSE_V1 p = new RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS_RESPONSE_V1(size);
            p.readFrom(this.inputStream);
            LOG.debug("VALUES {}", p.getValues());
        } else if (this.protocolVersion == 2) {

        } else {
            throw new IOException(" Unsupported protocol " + protocolVersion);
        }
    }

    public void start() {
        RTDE_CONTROL_PACKAGE_START_REQUEST request = new RTDE_CONTROL_PACKAGE_START_REQUEST();
        send(request);
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

        public void readFully(byte[] data) throws IOException {
            this.is.readFully(data);
        }

        @Override
        public void close() throws IOException {
            is.close();
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

    private static class RTDEGetURControlVersionRequest extends Package {

        public RTDEGetURControlVersionRequest() {
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

    private static class RTDE_REQUEST_PROTOCOL_VERSION_REQUEST extends Package {

        private int protocolVersion;

        public RTDE_REQUEST_PROTOCOL_VERSION_REQUEST() {
            super(RTDE_REQUEST_PROTOCOL_VERSION);
        }

        public RTDE_REQUEST_PROTOCOL_VERSION_REQUEST(int protocolVersion) {
            this();
            this.protocolVersion = protocolVersion;
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

    private static class RTDE_REQUEST_PROTOCOL_VERSION_RESPONSE extends Package {
        private boolean success;

        public RTDE_REQUEST_PROTOCOL_VERSION_RESPONSE() {
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

    private static class RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS_V1 extends Package {

        private byte[] data;

        public RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS_V1() {
            super(RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS);
        }

        public RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS_V1(String variableNames) {
            this();
            this.data = variableNames.getBytes(DEFAULT_CHARSET);
        }

        @Override
        public int getType() {
            return super.getType();
        }


        @Override
        public int getSize() {
            return 3 + data.length;
        }

        @Override
        public void writeTo(RTDEOutputStream os) throws IOException {
            writeHeader(os);
            os.write(data);
        }

        @Override
        public void readFrom(RTDEInputStream is) {

        }

    }

    private static class RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS_RESPONSE_V1 extends Package {

        private final int size;
        private byte[] data;

        public RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS_RESPONSE_V1(int size) {
            super(RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS);
            this.size = size;
            this.data = new byte[size - 3];
        }

        @Override
        public int getType() {
            return super.getType();
        }


        @Override
        public int getSize() {
            return size;
        }

        @Override
        public void writeTo(RTDEOutputStream os) throws IOException {
        }

        public String getValues() {
            return new String(data, DEFAULT_CHARSET);
        }

        @Override
        public void readFrom(RTDEInputStream is) throws IOException {
            is.readFully(data);
        }

    }

    private static class RTDE_CONTROL_PACKAGE_START_REQUEST extends Package {

        public RTDE_CONTROL_PACKAGE_START_REQUEST() {
            super(RTDE_CONTROL_PACKAGE_START);
        }

        @Override
        public int getType() {
            return super.getType();
        }

        @Override
        public int getSize() {
            return 3;
        }

        @Override
        public void writeTo(RTDEOutputStream os) throws IOException {
            writeHeader(os);
        }

        @Override
        public void readFrom(RTDEInputStream is) {

        }

    }

    private static class RTDE_CONTROL_PACKAGE_START_RESPONSE extends Package {
        private boolean success;

        public RTDE_CONTROL_PACKAGE_START_RESPONSE() {
            super(RTDE_CONTROL_PACKAGE_START);
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

    public static class RTDEUnsupportedPackage extends Package {
        private final int size;

        public RTDEUnsupportedPackage(int size, int type) {
            super(type);
            this.size = size;
        }

        @Override
        public int getSize() {
            return this.size;
        }

        @Override
        public void writeTo(RTDEOutputStream os) throws IOException {

        }

        @Override
        public void readFrom(RTDEInputStream is) throws IOException {
            byte[] data = new byte[size - 3];
            is.readFully(data);
        }
    }

}
