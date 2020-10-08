package com.my.mq.serializer;

import com.my.mq.common.spi.Spi;

@Spi
public interface Serialization {

    /**
     * 序列化
     */
    byte[] serializer(Object obj) throws Exception;

    /**
     * 反序列化
     */
    <T> Object deserializer(byte[] bytes, Class<T> clazz) throws Exception;

}
