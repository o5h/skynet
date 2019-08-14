package com.github.o5h.skynet.ur.rtde;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * See User manual for details.
 */
public class RTDEClient {

    private static final Logger LOG = LoggerFactory.getLogger(RTDEClient.class);


    public static final int PORT = 30004;

    private Socket socket;
    private RTDEOutputStream outputStream;
    private RTDEInputStream inputStream;

    public boolean connect(String host, int port, int timeout) {
        LOG.info("Connect to RTDE {} {}", host, port);
        if (this.socket != null) {
            return this.socket.isConnected();
        }
        try {
            this.socket = new Socket();
            this.socket.setKeepAlive(true);
            this.socket.setSoTimeout(timeout);
            this.socket.connect(new InetSocketAddress(host, port));
            LOG.info("Connected to RTDE {} {}", host, port);
            this.outputStream = new RTDEOutputStream(new DataOutputStream(socket.getOutputStream()));
            this.inputStream = new RTDEInputStream(new DataInputStream(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            LOG.error("Can't connect to RTDE.", e);
            disconnect();
            return false;
        }
    }

    private boolean disconnect() {
        if (socket == null) {
            return true;
        }
        closeSilently(this.outputStream);
        closeSilently(this.inputStream);
        closeSilently(socket);
        this.outputStream = null;
        this.inputStream = null;
        this.socket = null;
        LOG.info("Disconnected");
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

    public boolean send(Package msg) throws IOException {
        if (socket == null) {
            LOG.error("Can't send '{}'. Connection is closed", msg);
            return false;
        }
        msg.writeTo(this.outputStream);
        this.outputStream.flush();
        LOG.debug("Sent {}", msg);
        return true;
    }

    public Package receive() throws IOException {
        if (socket == null) {
            LOG.error("Can't read. Connection is closed");
            return null;
        }
        int size = this.inputStream.readUInt16();
        int type = this.inputStream.readUInt8();
        Package pkg;
        switch (type) {
            case Package.RTDE_REQUEST_PROTOCOL_VERSION:
                pkg = new RequestProtocolVersionResponse();
                break;
            default:
                pkg = new UnsupportedPackage(size, type);
                LOG.warn("Unsupported package type {}", type);
        }
        pkg.readFrom(inputStream);
        LOG.debug("Received {}",pkg);
        return pkg;
    }

    private boolean isConnected() {
        return this.socket != null && this.socket.isConnected();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        final RTDEClient client = new RTDEClient();
        client.connect("192.168.234.128", PORT, 0);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (client.isConnected()) {
                    try {
                        Package pkg = client.receive();
                        System.out.println(pkg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        client.send(new RequestProtocolVersionRequest(1));
        client.send(new GetURControlVersionRequest());
        Thread.sleep(10000);
        client.disconnect();
    }

}
