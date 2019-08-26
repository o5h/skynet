package com.github.o5h.skynet.examples.ur.dashboard;

import com.github.o5h.skynet.ur.dashboard.DashboardClient;

public class Main {
    public static void main(String[] args) {
        DashboardClient dashboard = new DashboardClient();
        if (args.length != 1) {
            System.out.println("Use host name or ip address as a parameter");
            return;
        }
        String host = "192.168.234.129";
        if (dashboard.connect(host)) {
            try {
                dashboard.powerOn();
                dashboard.brakeRelease();
                dashboard.load("default.urp");
                dashboard.play();
                DashboardClient.ProgramState state = dashboard.programState();
                System.out.println("Program state is  " + state);
            } finally {
                dashboard.disconnect();
            }
        }

    }
}
