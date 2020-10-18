package com.my.mq.common.enums;

public enum SerializeType {

    JSON((byte) 0),
    MYMQ((byte) 1),
    PROTOSTUFF((byte) 2);

    private byte code;

    SerializeType(byte code) {
        this.code = code;
    }

    public static SerializeType getByType(byte code) {
        for (SerializeType serializeType : SerializeType.values()) {
            if (serializeType.getCode() == code) {
                return serializeType;
            }
        }
        return null;
    }

    public byte getCode() {
        return code;
    }
}
