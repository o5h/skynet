package com.github.o5h.skynet.examples.ur.rtde;


import com.github.o5h.skynet.ur.rtde.RTDEClient;
import com.github.o5h.skynet.ur.rtde.RTDEOutputHandler;
import com.github.o5h.skynet.ur.rtde.RTDEOutputParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ExampleOutputV1 {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleOutputV1.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final RTDEClient client = new RTDEClient(new RTDEOutputHandler() {
            @Override
            public void onProtocolVersion(int protocolVersion) {
                if (protocolVersion != 1) {
                    throw new RuntimeException("Protocol v1 expected");
                }
                countDownLatch.countDown();
            }

            @Override
            public void onSetupOutputsResponse(RTDEOutputParam[] supported, RTDEOutputParam[] unsupported) {
                LOG.info("OUTPUTS {} {}", supported, unsupported);
            }

            @Override
            public void onData(RTDEOutputParam output, Object value) {
                LOG.info("DATA {} {}", output, value);
            }
        });

        if (client.connect("192.168.234.129", RTDEClient.PORT, 0)) {
            try {
                client.requestProtocolVersion(1);
                // to be sure that protocol version is selected properly
                countDownLatch.await();
                client.setupOutputsV1(
                        RTDEOutputParam.actual_TCP_pose,
                        RTDEOutputParam.target_TCP_pose,
                        RTDEOutputParam.actual_q,
                        RTDEOutputParam.elbow_velocity,
                        RTDEOutputParam.actual_main_voltage,
                        RTDEOutputParam.tool_output_mode);
                client.startOutput();
                Thread.sleep(100);
//            client.pause();
//            Thread.sleep(1000);
//            client.start();
//            Thread.sleep(1000);

            } finally {
                client.disconnect();
            }
        }
    }

}
