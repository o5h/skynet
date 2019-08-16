package com.github.o5h.skynet.examples.ur.rtde;


import com.github.o5h.skynet.ur.rtde.RTDEClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        final RTDEClient client = new RTDEClient(new RTDEClient.Handler() {
            @Override
            public void onConnect() {
                LOG.info("Connected");
            }

            @Override
            public void onDisconnect() {
                LOG.info("Disconnected");
            }

            @Override
            public void onProtocolVersion(int protocolVersion) {
                LOG.info("PROTOCOL VERSION {}", protocolVersion);
            }

            @Override
            public void onControlPackageStart() {
                LOG.info("Controller package START");
            }

            @Override
            public void onControlPackagePause() {
                LOG.info("Controller package PAUSE");
            }
        });
        client.connect("192.168.234.128", RTDEClient.PORT, 0);
        client.requestProtocolVersion(1);

        client.setupOutputsV1("actual_TCP_pose",
                "target_TCP_pose",
                "actual_q",
                "elbow_velocity");
        client.start();
        Thread.sleep(1000);
        client.pause();
        Thread.sleep(1000);
        client.start();
        Thread.sleep(1000);

        client.disconnect();
    }

}
