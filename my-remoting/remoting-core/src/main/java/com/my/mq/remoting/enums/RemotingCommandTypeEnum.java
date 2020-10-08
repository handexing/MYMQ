package com.my.mq.remoting.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @author handx
 * @version 1.0.0
 * @ClassName MessageTypeEnum.java
 * @Description 消息类型
 * @createTime 2020年09月30日 22:59:00
 */
public enum RemotingCommandTypeEnum {

    REQUEST_COMMAND((byte)1, "请求"), RESPONSE_COMMAND((byte)2, "相应");

    private byte type;
    private String describe;


    RemotingCommandTypeEnum(byte type, String describe) {
        this.type = type;
        this.describe = describe;
    }

    public int getType() {
        return type;
    }


    public String getDescribe() {
        return describe;
    }

    public static RemotingCommandTypeEnum getByType(byte type) {
        List<RemotingCommandTypeEnum> all = Arrays.asList(values());
        for (RemotingCommandTypeEnum item : all) {
            if (item.getType() == type) {
                return item;
            }
        }
        return null;
    }
}
