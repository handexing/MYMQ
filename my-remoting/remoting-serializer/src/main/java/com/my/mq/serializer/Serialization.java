package com.my.mq.serializer;

import com.my.mq.common.spi.Spi;

/**
 * @author: handx
 * @description: 序列化接口
 * @create 2020-10-14 14:22
 */
@Spi
public interface Serialization {

    /**
     * 编码
     */
    <T> byte[] encode(T obj) throws Exception;

    /**
     * 解码
     */
    <T> T decode(byte[] bytes, Class<T> clazz) throws Exception;

}
