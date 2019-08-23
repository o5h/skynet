package com.github.o5h.skynet.ur.rtde;

public enum RTDEDataType {
    NOT_FOUND(0),
    IN_USE(0),
    BOOL(1),
    UINT8(1),
    UINT32(4),
    UINT64(8),
    INT32(4),
    DOUBLE(8),
    VECTOR3D(8 * 3),
    VECTOR6D(8 * 6),
    VECTOR6INT32(4 * 6),
    VECTOR6UINT32(4 * 6),
    STRING(1);

    final int size;

    RTDEDataType(int size) {
        this.size = size;
    }

}
