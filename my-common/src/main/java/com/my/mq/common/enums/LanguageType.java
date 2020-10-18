package com.my.mq.common.enums;

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
