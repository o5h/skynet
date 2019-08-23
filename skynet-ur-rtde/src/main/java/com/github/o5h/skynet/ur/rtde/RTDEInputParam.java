package com.github.o5h.skynet.ur.rtde;

public enum RTDEInputParam {
    speed_slider_mask("speed_slider_mask", RTDEDataType.UINT32, "	0 = don't change speed slider with this input 1 = use speed_slider_fraction to set speed slider value	Introduced in version"),
    speed_slider_fraction("speed_slider_fraction", RTDEDataType.DOUBLE, "	new speed slider value"),
    standard_digital_output_mask("standard_digital_output_mask", RTDEDataType.UINT8, "	Standard digital output bit mask*"),
    standard_digital_output("standard_digital_output", RTDEDataType.UINT8, "	Standard digital outputs"),
    configurable_digital_output_mask("configurable_digital_output_mask", RTDEDataType.UINT8, "	Configurable digital output bit mask*"),
    configurable_digital_output("configurable_digital_output", RTDEDataType.UINT8, "	Configurable digital outputs"),
    tool_digital_output_mask("tool_digital_output_mask", RTDEDataType.UINT8, "	Tool digital outputs mask* Bits 0-1: mask, remaining bits are reserved for future use"),
    tool_digital_output("tool_digital_output", RTDEDataType.UINT8, "	Tool digital outputs  Bits 0-1: output state, remaining bits are reserved for future use"),
    standard_analog_output_mask("standard_analog_output_mask", RTDEDataType.UINT8, "	Standard analog output mask Bits 0-1: standard_analog_output_0 | standard_analog_output_1"),
    standard_analog_output_type("standard_analog_output_type", RTDEDataType.UINT8, "	Output domain {0=current[A], 1=voltage[V]} Bits 0-1: standard_analog_output_0 | standard_analog_output_1"),
    standard_analog_output_0("standard_analog_output_0", RTDEDataType.DOUBLE, "	Standard analog output 0 (ratio) [0..1]"),
    standard_analog_output_1("standard_analog_output_1", RTDEDataType.DOUBLE, "	Standard analog output 1 (ratio) [0..1]"),
    input_bit_registers0_to_31("input_bit_registers0_to_31", RTDEDataType.UINT32, "    General purpose bits    This range of the boolean input registers is reserved for FieldBus/PLC interface usage."),
    input_bit_registers32_to_63("input_bit_registers32_to_63", RTDEDataType.UINT32, "    General purpose bits This range of the boolean input registers is reserved for FieldBus/PLC interface usage."),
    input_bit_register_X("input_bit_register_X", RTDEDataType.BOOL, " 64 general purpose bits  X: [64..127] - The upper range of the boolean input registers can be used by external RTDE clients (i.e URCAPS).  3.9.0 / 5.3.0"),
    input_int_register_X("input_int_register_X", RTDEDataType.INT32, " 48 general purpose integer registers X: [0..23] - The lower range of the integer input registers is reserved for FieldBus/PLC interface usage. X: [24..47] - The upper range of the integer input registers can be used by external RTDE clients (i.e URCAPS). [0..23] 3.4.0 [24..47] 3.9.0 / 5.3.0"),
    input_double_register_X("input_double_register_X", RTDEDataType.DOUBLE, " 48 general purpose double registers X: [0..23] - The lower range of the double input registers is reserved for FieldBus/PLC interface usage. X: [24..47] - The upper range of the double input registers can be used by external RTDE clients (i.e URCAPS). [0..23] 3.4.0 [24..47] 3.9.0 / 5.3.0");

    public final String name;
    public final RTDEDataType type;
    public final String description;

    RTDEInputParam(String name, RTDEDataType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }
}
