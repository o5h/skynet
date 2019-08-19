package com.github.o5h.skynet.ur.rtde;

public interface RTDEProtocol {
    int RTDE_REQUEST_PROTOCOL_VERSION = 86; //V
    int RTDE_GET_URCONTROL_VERSION = 118;//	v
    int RTDE_TEXT_MESSAGE = 77;//	M
    int RTDE_DATA_PACKAGE = 85;//	U
    int RTDE_CONTROL_PACKAGE_SETUP_OUTPUTS = 79;//	O
    int RTDE_CONTROL_PACKAGE_SETUP_INPUTS = 73;// I
    int RTDE_CONTROL_PACKAGE_START = 83;//	S
    int RTDE_CONTROL_PACKAGE_PAUSE = 80; // P
}
