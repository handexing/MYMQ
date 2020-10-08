package com.my.mq.remoting.enums;

/**
 * @author handx
 * @version 1.0.0
 * @ClassName SerializerTypeEnum.java
 * @Description 序列化类型
 * @createTime 2020年09月30日 23:00:00
 */
public enum SerializerTypeEnum {

    KRYO((byte)1,"kryo"),JSON((byte)2,"json");

    private byte type;
    private String describe;

    SerializerTypeEnum(byte type, String describe) {
        this.type = type;
        this.describe = describe;
    }

    public byte getType() {
        return type;
    }
    public String getDescribe() {
        return describe;
    }

    public static SerializerTypeEnum valueOf(byte type) {
        for (SerializerTypeEnum serializeType : SerializerTypeEnum.values()) {
            if (serializeType.getType() == type) {
                return serializeType;
            }
        }
        return null;
    }
}
