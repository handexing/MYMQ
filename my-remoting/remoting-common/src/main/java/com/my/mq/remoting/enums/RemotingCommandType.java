package com.my.mq.remoting.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @author handx
 * @version 1.0.0
 * @ClassName RemotingCommandType.java
 * @Description 请求类型
 * @createTime 2020年10月28日 22:45:00
 */
public enum RemotingCommandType {

    REQUEST_COMMAND((byte)1, "请求"),
    REQUEST_ONE_WAY_COMMAND((byte)2,"单次请求，无回调"),
    RESPONSE_COMMAND((byte)6, "相应");

    private byte type;
    private String describe;


    RemotingCommandType(byte type, String describe) {
        this.type = type;
        this.describe = describe;
    }

    public byte getType() {
        return type;
    }


    public String getDescribe() {
        return describe;
    }

    public static RemotingCommandType getByType(byte type) {
        List<RemotingCommandType> all = Arrays.asList(values());
        for (RemotingCommandType item : all) {
            if (item.getType() == type) {
                return item;
            }
        }
        return null;
    }
}
