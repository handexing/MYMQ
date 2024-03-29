package com.my.mq.remoting.enums;

/**
 * @author handx
 * @version 1.0.0
 * @ClassName SerializeType.java
 * @Description 序列化方式类型
 * @createTime 2020年10月28日 22:45:00
 */
public enum SerializeType {

    JSON((byte) 0,"json"),
    MYMQ((byte) 1,"mymq"),
    PROTOSTUFF((byte) 2,"protostuff");

    private byte code;
    private String name;

    SerializeType(byte code,String name) {
        this.code = code;
        this.name = name;
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

    public String getName() {
        return name;
    }

}
