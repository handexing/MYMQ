package com.my.mq.remoting.protocol;

import com.my.mq.common.spi.ExtensionLoader;
import com.my.mq.remoting.enums.SerializerTypeEnum;
import com.my.mq.serializer.Serialization;
import com.my.mq.serializer.json.JsonSerializer;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author handx
 * @version 1.0.0
 * @ClassName RemoteCommand.java
 * @Description 协议头等信息
 * @createTime 2020年09月29日 23:38:00
 */
public class RemotingCommand {

    /**
     * 版本号
     */
    private int version;
    /**
     * 类型
     */
    private byte type;
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 消息体
     */
    private byte[] body;
    /**
     * 消息题长度
     */
    private int bodyLength;
    /**
     * 序列化类型
     */
    private byte serializerType;
    /**
     * 扩展字段
     */
    private Map<String, String> properties;

    public static RemotingCommand decode(final byte[] array) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        return decode(byteBuffer);
    }

    public static RemotingCommand decode(final ByteBuffer byteBuffer) {
        int length = byteBuffer.limit();
        int oriHeaderLen = byteBuffer.getInt();
        int headerLength = getHeaderLength(oriHeaderLen);

        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);

        RemotingCommand cmd = headerDecode(headerData, getProtocolType(oriHeaderLen));

        int bodyLength = length - 4 - headerLength;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            byteBuffer.get(bodyData);
        }
        cmd.body = bodyData;

        return cmd;
    }

    private static RemotingCommand headerDecode(byte[] headerData, SerializerTypeEnum type) {
        switch (type) {
            case JSON:
//                RemotingCommand resultJson = RemotingSerializable.decode(headerData, RemotingCommand.class);
//                resultJson.setSerializeTypeCurrentRPC(type);
                return null;
            case KRYO:
//                RemotingCommand resultRMQ = RocketMQSerializable.rocketMQProtocolDecode(headerData);
//                resultRMQ.setSerializeTypeCurrentRPC(type);
                return null;
            default:
                break;
        }

        return null;
    }

    public static int getHeaderLength(int length) {
        return length & 0xFFFFFF;
    }

    public static SerializerTypeEnum getProtocolType(int source) {
        return SerializerTypeEnum.valueOf((byte) ((source >> 24) & 0xFF));
    }

    public ByteBuffer encode() {
        // 1> header length size
        int length = 4;

        // 2> header data length
        byte[] headerData = this.headerEncode();
        length += headerData.length;

        // 3> body data length
        if (this.body != null) {
            length += body.length;
        }

        ByteBuffer result = ByteBuffer.allocate(4 + length);

        // length
        result.putInt(length);

        // header length
        result.put(markProtocolType(headerData.length, SerializerTypeEnum.valueOf(serializerType)));

        // header data
        result.put(headerData);

        // body data;
        if (this.body != null) {
            result.put(this.body);
        }

        result.flip();

        return result;
    }

    private byte[] headerEncode() {
//        this.makeCustomHeaderToNet();
        SerializerTypeEnum type = SerializerTypeEnum.valueOf(serializerType);
        switch (type){
            case JSON:
//                ExtensionLoader<JsonSerializer> jsonSerializer = ExtensionLoader.getExtensionLoader(JsonSerializer.class);
                return null;
            case KRYO:
                return null;
            default:
                break;
        }
//        if (SerializerTypeEnum.JSON == serializerType) {
//            return RocketMQSerializable.rocketMQProtocolEncode(this);
//        } else {
//            return RemotingSerializable.encode(this);
//        }
        return null;
    }

    public static byte[] markProtocolType(int source, SerializerTypeEnum type) {
        byte[] result = new byte[4];

        result[0] = type.getType();
        result[1] = (byte) ((source >> 16) & 0xFF);
        result[2] = (byte) ((source >> 8) & 0xFF);
        result[3] = (byte) (source & 0xFF);
        return result;
    }


    public int getSerializerType() {
        return serializerType;
    }

    public void setSerializerType(byte serializerType) {
        this.serializerType = serializerType;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte Type) {
        this.type = type;
    }

}
