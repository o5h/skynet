package com.github.o5h.skynet.ur.rtde;

public enum RTDEOutputParam {
    timestamp("timestamp", RTDEDataType.DOUBLE, "Time elapsed since the controller was started [s]."),
    target_q("target_q", RTDEDataType.VECTOR6D, "Target joint positions."),
    target_qd("target_qd", RTDEDataType.VECTOR6D, "Target joint velocities."),
    target_qdd("target_qdd", RTDEDataType.VECTOR6D, "Target joint accelerations."),
    target_current("target_current", RTDEDataType.VECTOR6D, "Target joint currents."),
    target_moment("target_moment", RTDEDataType.VECTOR6D, "Target joint moments (torques)."),
    actual_q("actual_q", RTDEDataType.VECTOR6D, "Actual joint positions."),
    actual_qd("actual_qd", RTDEDataType.VECTOR6D, "Actual joint velocities."),
    actual_current("actual_current", RTDEDataType.VECTOR6D, "Actual joint currents."),
    joint_control_output("joint_control_output", RTDEDataType.VECTOR6D, "Joint control currents."),
    actual_TCP_pose("actual_TCP_pose", RTDEDataType.VECTOR6D, "Actual Cartesian coordinates of the tool: (x,y,z,rx,ry,rz), " +
            "where rx, ry and rz is a rotation vector representation of the tool orientation."),
    actual_TCP_speed("actual_TCP_speed", RTDEDataType.VECTOR6D, "Actual speed of the tool given in Cartesian coordinates"),
    actual_TCP_force("actual_TCP_force", RTDEDataType.VECTOR6D, "Generalized forces in the TCP"),
    target_TCP_pose("target_TCP_pose", RTDEDataType.VECTOR6D, "Target Cartesian coordinates of the tool: (x,y,z,rx,ry,rz), where rx, ry and rz is a rotation vector representation of the tool orientation"),
    target_TCP_speed("target_TCP_speed", RTDEDataType.VECTOR6D, "Target speed of the tool given in Cartesian coordinates"),
    actual_digital_input_bits("actual_digital_input_bits", RTDEDataType.UINT64, "Current state of the digital inputs. 0-7: Standard, 8-15: Configurable, 16-17: Tool"),
    joint_temperatures("joint_temperatures", RTDEDataType.VECTOR6D, "Temperature of each joint in degrees Celsius"),
    actual_execution_time("actual_execution_time", RTDEDataType.DOUBLE, "Controller real-time thread execution time"),
    robot_mode("robot_mode", RTDEDataType.INT32, "Robot mode. Please see Remote Control Via TCP/IP - 16496,"),
    joint_mode("joint_mode", RTDEDataType.VECTOR6INT32, "Joint control modes  Please  see Remote Control Via TCP/IP - 16496"),
    safety_mode("safety_mode", RTDEDataType.INT32, "Safety mode. Please see Remote Control Via TCP/IP - 16496"),
    safety_status("safety_status", RTDEDataType.INT32, "Safety status. 3.10.0/5.4.0"),
    actual_tool_accelerometer("actual_tool_accelerometer", RTDEDataType.VECTOR3D, "Tool x, y and z accelerometer values"),
    speed_scaling("speed_scaling", RTDEDataType.DOUBLE, "Speed scaling of the trajectory limiter"),
    target_speed_fraction("target_speed_fraction", RTDEDataType.DOUBLE, "Target speed fraction"),
    actual_momentum("actual_momentum", RTDEDataType.DOUBLE, "Norm of Cartesian linear momentum"),
    actual_main_voltage("actual_main_voltage", RTDEDataType.DOUBLE, "Safety Control Board: Main voltage"),
    actual_robot_voltage("actual_robot_voltage", RTDEDataType.DOUBLE, "Safety Control Board: Robot voltage (48V)"),
    actual_robot_current("actual_robot_current", RTDEDataType.DOUBLE, "Safety Control Board: Robot current"),
    actual_joint_voltage("actual_joint_voltage", RTDEDataType.VECTOR6D, "Actual joint voltages"),
    actual_digital_output_bits("actual_digital_output_bits", RTDEDataType.UINT64, "Current state of the digital outputs. 0-7: Standard, 8-15: Configurable, 16-17: Tool"),
    runtime_state("runtime_state", RTDEDataType.UINT32, "Program state"),
    elbow_position("elbow_position", RTDEDataType.VECTOR3D, "Position of robot elbow in Cartesian Base Coordinates. 3.5.0/5.0.0"),
    elbow_velocity("elbow_velocity", RTDEDataType.VECTOR3D, "Velocity of robot elbow in Cartesian Base Coordinates\t3.5.0/5.0.0"),
    robot_status_bits("robot_status_bits", RTDEDataType.UINT32, "Bits 0-3: Is power on | Is program running | Is teach button pressed | Is power button pressed"),
    safety_status_bits("safety_status_bits", RTDEDataType.UINT32, "Bits 0-10: Is normal mode | Is reduced mode | | Is protective stopped | Is recovery mode | Is safeguard stopped | Is system emergency stopped | Is robot emergency stopped | Is emergency stopped | Is violation | Is fault | Is stopped due to safety"),
    analog_io_types("analog_io_types", RTDEDataType.UINT32, "Bits 0-3: analog input 0 | analog input 1 | analog output 0 | analog output 1, {0=current[A], 1=voltage[V]}"),
    standard_analog_input0("standard_analog_input0", RTDEDataType.DOUBLE, "Standard analog input 0 [A or V]"),
    standard_analog_input1("standard_analog_input1", RTDEDataType.DOUBLE, "Standard analog input 1[A or V]"),
    standard_analog_output0("standard_analog_output0", RTDEDataType.DOUBLE, "Standard analog output 0[A or V]"),
    standard_analog_output1("standard_analog_output1", RTDEDataType.DOUBLE, "Standard analog output 1[A or V]"),
    io_current("io_current", RTDEDataType.DOUBLE, "I/O current[A]"),
    euromap67_input_bits("euromap67_input_bits", RTDEDataType.UINT32, "Euromap67 input bits"),
    euromap67_output_bits("euromap67_output_bits", RTDEDataType.UINT32, "Euromap67 output bits"),
    euromap67_24V_voltage("euromap67_24V_voltage", RTDEDataType.DOUBLE, "Euromap 24V voltage [V]"),
    euromap67_24V_current("euromap67_24V_current", RTDEDataType.DOUBLE, "Euromap 24V current [A]"),
    tool_mode("tool_mode", RTDEDataType.UINT32, "Tool mode.    Please see Remote Control Via TCP/IP - 16496"),
    tool_analog_input_types("tool_analog_input_types", RTDEDataType.UINT32, "Output domain {0=current[A], 1=voltage[V]} Bits 0-1: tool_analog_input_0 | tool_analog_input_1"),
    tool_analog_input0("tool_analog_input0", RTDEDataType.DOUBLE, "Tool analog input 0[A or V]"),
    tool_analog_input1("tool_analog_input1", RTDEDataType.DOUBLE, "Tool analog input 1[A or V]"),
    tool_output_voltage("tool_output_voltage", RTDEDataType.INT32, "Tool output voltage[V]"),
    tool_output_current("tool_output_current", RTDEDataType.DOUBLE, "Tool current[A]"),
    tool_temperature("tool_temperature", RTDEDataType.DOUBLE, "Tool temperature in degrees Celsius"),
    tcp_force_scalar("tcp_force_scalar", RTDEDataType.DOUBLE, "TCP force scalar[N]"),
    output_bit_registers0_to_31("output_bit_registers0_to_31", RTDEDataType.UINT32, "General purpose bits"),
    output_bit_registers32_to_63("output_bit_registers32_to_63", RTDEDataType.UINT32, "General purpose bits"),
    output_bit_register_X("output_bit_register_X", RTDEDataType.BOOL, "64 general purpose bits   X: [64..127] - The upper range of the boolean output registers can be used by external RTDE clients (i.e URCAPS). 3.9.0 / 5.3.0"),
    output_int_register_X("output_int_register_X", RTDEDataType.INT32, "48 general purpose integer registers \n" +
            "X: [0..23] - The lower range of the integer output registers is reserved for FieldBus/PLC interface usage.\n" +
            "X: [24..47] - The upper range of the integer output registers can be used by external RTDE clients (i.e URCAPS).\n" +
            "[0..23] 3.4.0\n" +
            "[24..47] 3.9.0 / 5.3.0\n"),
    output_double_register_X("output_double_register_X", RTDEDataType.DOUBLE, "48 general purpose double registers\n" +
            "X: [0..23] - The lower range of the double output registers is reserved for FieldBus/PLC interface usage.\n" +
            "X: [24..47] - The upper range of the double output registers can be used by external RTDE clients (i.e URCAPS).\n" +
            "[0..23] 3.4.0\n" +
            "[24..47] 3.9.0 / 5.3.0"),
    input_bit_registers0_to_31("input_bit_registers0_to_31", RTDEDataType.UINT32, "General purpose bits\n" +
            "This range of the boolean output registers is reserved for FieldBus/PLC interface usage.\n" +
            "3.4.0"),
    input_bit_registers32_to_63("input_bit_registers32_to_63", RTDEDataType.UINT32, "General purpose bits\n" +
            "This range of the boolean output registers is reserved for FieldBus/PLC interface usage.\n" +
            "3.4.0"),
    input_bit_register_x("input_bit_register_x", RTDEDataType.BOOL, "64 general purpose bits\n" +
            "X: [64..127] - The upper range of the boolean output registers can be used by external RTDE clients (i.e URCAPS).\n" +
            "3.9.0 / 5.3.0"),
    input_int_register_x("input_int_register_x", RTDEDataType.INT32, "([0 .. 48]) 48 general purpose integer registers\n" +
            "X: [0..23] - The lower range of the integer input registers is reserved for FieldBus/PLC interface usage.\n" +
            "X: [24..47] - The upper range of the integer input registers can be used by external RTDE clients (i.e URCAPS).\n" +
            "[0..23] 3.4.0\n" +
            "[24..47] 3.9.0 / 5.3.0"),
    input_double_register_x("input_double_register_x", RTDEDataType.DOUBLE, "([0 .. 48])\n" +
            "48 general purpose double registers\n" +
            "X: [0..23] - The lower range of the double input registers is reserved for FieldBus/PLC interface usage.\n" +
            "X: [24..47] - The upper range of the double input registers can be used by external RTDE clients (i.e URCAPS).\n" +
            "[0..23] 3.4.0\n" +
            "[24..47] 3.9.0 / 5.3.0"),

    tool_output_mode("tool_output_mode", RTDEDataType.UINT8, "The current output mode\n5.2.0"),
    tool_digital_output0_mode("tool_digital_output0_mode", RTDEDataType.UINT8, "The current mode of digital output 0 \n 5.2.0"),
    tool_digital_output1_mode("tool_digital_output1_mode", RTDEDataType.UINT8, "The current mode of digital output 1 5.2.0"),
    // input_bit_registers0_to_31("input_bit_registers0_to_31", RTDEDataType.UINT32, "General purpose bits(input read back) 3.4.0"),
    // input_bit_registers32_to_63("input_bit_registers32_to_63", RTDEDataType.UINT32, "General purpose bits(input read back) 3.4.0"),
    input_int_register_X("input_int_register_X", RTDEDataType.INT32, "24 general purpose integer registers (input read back)\n" +
            "X: [0..23] 3.4.0"),
    input_double_register_X("input_double_register_X", RTDEDataType.DOUBLE, "24 general purpose double registers (input read back)" +
            "X: [0..23]\t3.4.0");


    public final String name;
    public final RTDEDataType type;
    public final String description;

    RTDEOutputParam(String name, RTDEDataType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }


}
