package com.my.mq.common.consts;

/**
 * @author handx
 * @version 1.0.0
 * @ClassName MqConstants.java
 * @Description 公共常量配置
 * @createTime 2020年10月28日 22:45:00
 */
public class MqConstants {

    public static final boolean NETTY_POOLED_BYTE_BUF_ALLOCATOR_ENABLE = false;
    public static final int CLIENT_ASYNC_SEMAPHORE_VALUE = 65535;
    public static final int CLIENT_ONEWAY_SEMAPHORE_VALUE = 65535;
    public static int socketSndbufSize = 65535;
    public static int socketRcvbufSize = 65535;
}
