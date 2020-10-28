package com.my.mq.remoting.enums;

/**
 * @author handx
 * @version 1.0.0
 * @ClassName LanguageType.java
 * @Description 支持的语言
 * @createTime 2020年10月28日 22:45:00
 */
public enum LanguageType {

    JAVA((byte) 0),
    CPP((byte) 1),
    PYTHON((byte) 2),
    HTTP((byte) 3),
    GO((byte) 4),
    PHP((byte) 5);

    private byte type;

    LanguageType(byte type) {
        this.type = type;
    }

    public static LanguageType getByType(byte type) {
        for (LanguageType languageCode : LanguageType.values()) {
            if (languageCode.getType() == type) {
                return languageCode;
            }
        }
        return null;
    }

    public byte getType() {
        return type;
    }
}
