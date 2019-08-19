package com.github.o5h.skynet.ur.rtde;

public enum RTDEDataType {
    NOT_FOUND,
    BOOL,    //0 = False, everything else = True	8
    UINT8,   //unsigned integer	8
    UINT32,  //unsigned integer	32
    UINT64,  //unsigned integer	64
    INT32,   //signed integer, two's complement	32
    DOUBLE,  //IEEE 754 floating point	64
    VECTOR3D,//	3xDOUBLE	3x64
    VECTOR6D,//	6xDOUBLE	6x64
    VECTOR6INT32,//	6xINT32	6x32
    VECTOR6UINT32,//	6xUINT32	6x32
    STRING,  //ASCII char array	lengthx8
}
