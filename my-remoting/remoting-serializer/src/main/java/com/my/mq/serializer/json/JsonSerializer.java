package com.my.mq.serializer.json;

import com.alibaba.fastjson.JSON;
import com.my.mq.serializer.Serialization;

import java.nio.charset.Charset;

/**
 * @author handx
 * @version 1.0.0
 * @ClassName JsonSerializer.java
 * @Description json序列化
 * @createTime 2020年10月07日 21:52:00
 */
public class JsonSerializer implements Serialization {

    private final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    @Override
    public byte[] serializer(Object obj) throws Exception {
        final String json = toJson(obj, false);
        if (json != null) {
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }


    @Override
    public <T> Object deserializer(byte[] bytes, Class<T> clazz) throws Exception {
        final String json = new String(bytes, CHARSET_UTF8);
        return fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }

    public static String toJson(final Object obj, boolean prettyFormat) {
        return JSON.toJSONString(obj, prettyFormat);
    }
}
