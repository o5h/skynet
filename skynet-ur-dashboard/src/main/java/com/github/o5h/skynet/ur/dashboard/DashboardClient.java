package com.github.o5h.skynet.ur.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * See User manual for details.
 */
public class DashboardClient {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardClient.class);
    private static final int DASHBOARD_PORT = 29999;
    private static final long TIMEOUT_IN_SECONDS = 5;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

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

    public boolean connect(String host, int port, int timeout) {
        if (this.socket != null) {
            return this.socket.isConnected();
        }
        try {
            this.socket = new Socket();
            this.socket.setKeepAlive(true);
            this.socket.setSoTimeout(timeout);
            this.socket.connect(new InetSocketAddress(host, port));
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String welcomeMsg = this.reader.readLine();
            LOG.info(welcomeMsg);
            return true;
        } catch (SocketException e) {
            LOG.error("Can't connect to dashboard. SocketException", e);
            disconnect();
            return false;
        } catch (UnsupportedEncodingException e) {
            LOG.error("Can't connect to dashboard. UnsupportedEncodingException", e);
            disconnect();
            return false;
        } catch (UnknownHostException e) {
            LOG.error("Can't connect to dashboard. UnknownHostException", e);
            disconnect();
            return false;
        } catch (IOException e) {
            LOG.error("Can't connect to dashboard. IOException", e);
            disconnect();
            return false;
        }
    }

    /**
     * Closes connection
     */
    public boolean disconnect() {
        if (socket == null) {
            return true;
        }
        if (socket.isConnected()) {
            send("quit");
        }
        closeSilently(reader);
        closeSilently(writer);
        closeSilently(socket);
        reader = null;
        writer = null;
        socket = null;
        return true;
    }

    protected synchronized String send(final String request) {
        if (socket == null) {
            LOG.error("Can't send '{}'. Connection is closed", request);
            return "";
        }
        LOG.debug("Request: '{}'", request);
        writer.print(request);
        writer.print('\n');
        writer.flush();
        try {
            String response = reader.readLine();
            LOG.debug("Response: '{}'", response);
            return response;
        } catch (IOException e) {
            LOG.warn("Can't read response for '{}'", request, e);
            return "";
        }
    }

    public enum UserRole {
        UNDEFINED(""),
        PROGRAMMER("programmer"),
        OPERATOR("operator"),
        NONE("none"),
        LOCKED("locked"),
        RESTRICTED("restricted");
        private final String name;

        UserRole(String name) {
            this.name = name;
        }
    }

    public enum SafetyMode {
        UNDEFINED,
        NORMAL,
        REDUCED,
        PROTECTIVE_STOP,
        RECOVERY,
        SAFEGUARD_STOP,
        SYSTEM_EMERGENCY_STOP,
        ROBOT_EMERGENCY_STOP,
        VIOLATION,
        FAULT
    }

    public enum ProgramState {
        UNDEFINED,
        STOPPED,
        PLAYING,
        PAUSED //CB3, CB3.1 only
    }

    public enum RobotMode {
        UNDEFINED,

        //CB2
        NO_CONTROLLER_MODE,// = -1
        ROBOT_RUNNING_MODE,// = 0 (This is "normal" mode)
        ROBOT_FREEDRIVE_MODE,//= 1
        ROBOT_READY_MODE,//= 2
        ROBOT_INITIALIZING_MODE,//= 3
        ROBOT_SECURITY_STOPPED_MODE,//= 4
        ROBOT_EMERGENCY_STOPPED_MODE,//= 5
        ROBOT_FAULT_MODE,//= 6
        ROBOT_NO_POWER_MODE,//= 7
        ROBOT_NOT_CONNECTED_MODE,//= 8
        ROBOT_SHUTDOWN_MODE,//= 9

        //CB3, E-Series
        NO_CONTROLLER,
        DISCONNECTED,
        CONFIRM_SAFETY,
        BOOTING,
        POWER_OFF,
        POWER_ON,
        IDLE,
        BACKDRIVE,
        RUNNING
    }

    /**
     * E-Series Only
     */
    public enum OperationalMode {
        MANUAL("manual"),
        AUTOMATIC("automatic");

        private final String name;

        OperationalMode(String name) {
            this.name = name;
        }
    }

    public DashboardClient() {
    }

    public boolean connect(String host) {
        return connect(host, DASHBOARD_PORT, (int) TimeUnit.SECONDS.toMillis(DashboardClient.TIMEOUT_IN_SECONDS));
    }

    /**
     * Load the specified program.
     */
    public boolean load(String programFilePath) {
        String response = send("load " + programFilePath);
        return response.startsWith("Loading program:");
    }

    /**
     * Starts program
     */
    public boolean play() {
        return "Starting program".equals(send("play"));
    }

    /**
     * Stops running program.
     */
    public boolean stop() {
        return "Stopped".equals(send("stop"));
    }

    /**
     * Pauses the running program and returns when pausing is completed.
     */
    public boolean pause() {
        return "Pausing program".equals(send("pause"));
    }

    /**
     * Shuts down and turns off robot and controller
     */
    public boolean shutdown() {
        return "Shutting down".equals(send("shutdown"));
    }

    /**
     * Execution state enquiry
     */
    public boolean running() {
        return "Program running: True".equals(send("running"));
    }

    /**
     * Robot mode
     */
    public RobotMode robotMode() {
        String response = send("robotmode");

        if ("-1".equals(response)) {
            return RobotMode.NO_CONTROLLER_MODE;
        } else if ("0".equals(response)) {
            return RobotMode.ROBOT_RUNNING_MODE;
        } else if ("1".equals(response)) {
            return RobotMode.ROBOT_FREEDRIVE_MODE;
        } else if ("2".equals(response)) {
            return RobotMode.ROBOT_READY_MODE;
        } else if ("3".equals(response)) {
            return RobotMode.ROBOT_INITIALIZING_MODE;
        } else if ("4".equals(response)) {
            return RobotMode.ROBOT_SECURITY_STOPPED_MODE;
        } else if ("5".equals(response)) {
            return RobotMode.ROBOT_EMERGENCY_STOPPED_MODE;
        } else if ("6".equals(response)) {
            return RobotMode.ROBOT_FAULT_MODE;
        } else if ("7".equals(response)) {
            return RobotMode.ROBOT_NO_POWER_MODE;
        } else if ("8".equals(response)) {
            return RobotMode.ROBOT_NOT_CONNECTED_MODE;
        } else if ("9".equals(response)) {
            return RobotMode.ROBOT_SHUTDOWN_MODE;
        } else {
            try {
                return RobotMode.valueOf(response.substring("Robotmode: ".length()));
            } catch (Exception e) {
                LOG.warn("Can't getValue robot mode {}", e.getMessage(), e);
            }
        }
        return RobotMode.UNDEFINED;
    }

    /**
     * Returns path to loaded program file
     */
    public String getLoadedProgram() {
        String response = send("getValue loaded program");
        if (!response.startsWith("Loaded program: ")) {
            return "";
        }
        return response.substring("Loaded program: ".length());
    }

    /**
     * The popup-text will be translated to the selected language, if the text exists in the language file
     */
    public boolean popup(String msg) {
        return "showing popup".equals(send("popup " + msg));
    }

    /**
     * Closes the popup.
     */
    public boolean closePopup() {
        return "closing popup".equals(send("close popup"));
    }

    public boolean addToLog(String msg) {
        return "Added log message".equals(send("addToLog " + msg));
    }

    public boolean isProgramSaved() {
        return send("isProgramSaved").startsWith("true");
    }

    /**
     * Returns program state.
     */
    public ProgramState programState() {
        String response = send("programState");
        try {
            String stateName = response.split(" ")[0];
            return ProgramState.valueOf(stateName);
        } catch (Exception e) {
            LOG.warn("Can't getValue program state");
        }
        return ProgramState.UNDEFINED;
    }

    /**
     * Returns the version of the Polyscope software
     */
    public String polyscopeVersion() {
        return send("PolyscopeVersion");
    }

    /**
     * Controls the operational mode.
     * IMPORTANT: E-Series only
     */
    public boolean setOperationalMode(OperationalMode mode) {
        String response = send("set operational mode " + mode.name);
        return response.startsWith("Setting operational mode:");
    }

    /**
     * Controls the operational mode.
     * IMPORTANT: E-Series only
     */
    public boolean clearOperationalMode() {
        return "operational mode is no longer controlled by Dashboard Server".equals(
                send("clear operational mode"));
    }

    /**
     * Control user role.
     * IMPORTANT: CB-Series only.
     */
    public boolean setUserRole(UserRole role) {
        String response = send("setuserrole " + role.name);
        return ("Setting user role:" + role.name).equals(response);
    }

    /**
     * Powers on the robot arm
     */
    public boolean powerOn() {
        return "Powering on".equals(send("power on"));
    }

    /**
     * Powers off the robot arm
     */
    public boolean powerOff() {
        return "Powering off".equals(send("power off"));
    }

    /**
     * Releases the brakes
     */
    public boolean brakeRelease() {
        return "Brake releasing".equals(send("brake release"));
    }

    /**
     * Safety mode enquiry
     */
    public SafetyMode safetymode() {
        String response = send("safetymode");
        try {
            if (response.startsWith("Safetymode:")) {
                String modeName = response.substring("Safetymode:".length());
                return SafetyMode.valueOf(modeName.trim());
            }
        } catch (Exception e) {
            LOG.warn("Can't getValue safety mode");
        }
        return SafetyMode.UNDEFINED;
    }

    /**
     * Closes the current popup and unlocks protective stop.
     */
    public boolean unlockProtectiveStop() {
        return "Protective stop releasing".equals(send("unlock protective stop"));
    }

    /**
     * Closes a safety popup
     */
    public boolean closeSafetyPopup() {
        return "closing safety popup".equals(send("close safety popup"));
    }

    public boolean restartSafety() {
        return "Restarting safety".equals(send("restart safety"));
    }

    public boolean loadInstallation(String installationFilePath) {
        String response = send("load installation " + installationFilePath);
        return response.startsWith("Loading installation:");
    }

    public static void main(String[] args) {
        DashboardClient dashboard = new DashboardClient();
        try {
            dashboard.connect("localhost");
            dashboard.powerOn();
            dashboard.brakeRelease();
            Thread.sleep(1000);
            dashboard.setUserRole(UserRole.UNDEFINED); // is it really works??
            LOG.info("SafetyMode:= {}", dashboard.safetymode());
            dashboard.loadInstallation("default.installation");
            dashboard.pause();
            dashboard.play();
            dashboard.stop();
            dashboard.popup("Stop");
            Thread.sleep(1000); //should wait for actual popup
            dashboard.closePopup();
            dashboard.addToLog("Log record");
            LOG.info("isProgramSaved {}", dashboard.isProgramSaved());

            LOG.info("unlockProtectiveStop := {}", dashboard.unlockProtectiveStop());
            dashboard.restartSafety();
            LOG.info("dashboard.safetymode() := {}", dashboard.safetymode());

            LOG.info("getLoadedProgram:= {}", dashboard.getLoadedProgram());
            LOG.info("RobotMode:= {}", dashboard.robotMode());
            //dashboard.powerOff();
            LOG.info("PolyscopeVersion:= {}", dashboard.polyscopeVersion());
            LOG.info("ProgramState:= {}", dashboard.programState());
            //dashboard.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            dashboard.disconnect();
        }
    }

}
