package com.github.o5h.skynet.examples.ur.rtde;

import com.github.o5h.skynet.ur.rtde.RTDEClient;
import com.github.o5h.skynet.ur.rtde.RTDEInputParam;
import com.github.o5h.skynet.ur.rtde.RTDEOutputHandler;
import com.github.o5h.skynet.ur.rtde.RTDEOutputParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ExampleInput {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleInput.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        final int[] ids = {0};
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final RTDEClient client = new RTDEClient(new RTDEOutputHandler(){
            @Override
            public void onSetupInputsResponse(int id, RTDEInputParam[] supported, RTDEInputParam[] unsupported) {
                LOG.info("INPUTS {} {} {}", id, supported, unsupported);
                ids[0] = id;
                countDownLatch.countDown();
            }
        });
        if (client.connect("192.168.234.129", RTDEClient.PORT, 0)) {
            try {
                client.setupInputs(
                        RTDEInputParam.speed_slider_mask,
                        RTDEInputParam.speed_slider_fraction);
                countDownLatch.await();
                double value =0;
                for (int i=0; i<100; i++ ){
                    client.sendData(ids[0],
                            1,
                            value);
                    Thread.sleep(100);
                    value+=0.01;
                }
            } finally {
                client.disconnect();
            }
        }
    }
}
