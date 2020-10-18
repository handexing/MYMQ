package com.my.mq.remoting.protocol;

import com.my.mq.remoting.enums.LanguageType;
import com.my.mq.remoting.enums.RemotingCommandType;
import com.my.mq.remoting.enums.SerializeType;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RemotingCommand {

    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    private static AtomicInteger sequenceId = new AtomicInteger(0);
    private static SerializeType serializeTypeConfigInThisServer = SerializeType.JSON;

    private int code;
    private int version;
    private String remark;
    private HashMap<String, String> extFields;
    private int requestId;
    private LanguageType language;
    private SerializeType serializeType;
    private RemotingCommandType remotingCommandType;
    private transient byte[] body;

    protected RemotingCommand() {
    }

    public RemotingCommand(Builder builder) {
        super();
        this.code = builder.code;
        this.version = builder.version;
        this.remark = builder.remark;
        this.extFields = builder.extFields;
        this.requestId = builder.requestId;
        this.language = builder.language;
        this.serializeType = builder.serializeType;
        this.remotingCommandType = builder.remotingCommandType;
        this.body = builder.body;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int code;
        private int version = 0;
        private String remark;
        private HashMap<String, String> extFields;
        private int requestId = sequenceId.getAndIncrement();
        private LanguageType language = LanguageType.JAVA;
        private SerializeType serializeType = serializeTypeConfigInThisServer;
        private RemotingCommandType remotingCommandType;
        private transient byte[] body;

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        public Builder remark(String remark) {
            this.remark = remark;
            return this;
        }

        public Builder addExtFields(String key, String value) {
            if (null == this.extFields) {
                this.extFields = new HashMap<String, String>();
            }
            this.extFields.put(key, value);
            return this;
        }

        public Builder requestId(int requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder LanguageType(LanguageType language) {
            this.language = language;
            return this;
        }

        public Builder serializeType(SerializeType serializeType) {
            this.serializeType = serializeType;
            return this;
        }

        public Builder remotingCommandType(RemotingCommandType remotingCommandType) {
            this.remotingCommandType = remotingCommandType;
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public RemotingCommand build() {
            return new RemotingCommand(this);
        }
    }

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

    public static int getHeaderLength(int length) {
        return length & 0xFFFFFF;
    }

    private static RemotingCommand headerDecode(byte[] headerData, SerializeType type) {
        switch (type) {
            case JSON:
                RemotingCommand resultJson = RemotingSerializable.decode(headerData, RemotingCommand.class);
                resultJson.setSerializeType(type);
                return resultJson;
            case MYMQ:
                RemotingCommand resultRMQ = MyMQSerializable.rocketMQProtocolDecode(headerData);
                resultRMQ.setSerializeType(type);
                return resultRMQ;
            case PROTOSTUFF:
                RemotingCommand resultKryo = ProtostuffSerializable.decode(headerData, RemotingCommand.class);
                resultKryo.setSerializeType(type);
                return resultKryo;
            default:
                break;
        }
        return null;
    }

    private byte[] headerEncode() {
        switch (serializeType) {
            case JSON:
                return RemotingSerializable.encode(this);
            case MYMQ:
                return MyMQSerializable.rocketMQProtocolEncode(this);
            case PROTOSTUFF:
                return ProtostuffSerializable.encode(this);
            default:
                break;
        }
        return null;
    }


    public static SerializeType getProtocolType(int source) {
        return SerializeType.getByType((byte) ((source >> 24) & 0xFF));
    }

    private static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static byte[] markProtocolType(int source, SerializeType type) {
        byte[] result = new byte[4];

        result[0] = type.getCode();
        result[1] = (byte) ((source >> 16) & 0xFF);
        result[2] = (byte) ((source >> 8) & 0xFF);
        result[3] = (byte) (source & 0xFF);
        return result;
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
        result.put(markProtocolType(headerData.length, serializeType));

        // header data
        result.put(headerData);

        // body data;
        if (this.body != null) {
            result.put(this.body);
        }

        result.flip();

        return result;
    }

    public ByteBuffer encodeHeader() {
        return encodeHeader(this.body != null ? this.body.length : 0);
    }

    public ByteBuffer encodeHeader(final int bodyLength) {
        // 1> header length size
        int length = 4;

        // 2> header data length
        byte[] headerData;
        headerData = this.headerEncode();

        length += headerData.length;

        // 3> body data length
        length += bodyLength;

        ByteBuffer result = ByteBuffer.allocate(4 + length - bodyLength);

        // length
        result.putInt(length);

        // header length
        result.put(markProtocolType(headerData.length, serializeType));

        // header data
        result.put(headerData);

        result.flip();

        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public HashMap<String, String> getExtFields() {
        return extFields;
    }

    public void setExtFields(HashMap<String, String> extFields) {
        this.extFields = extFields;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public LanguageType getLanguage() {
        return language;
    }

    public void setLanguage(LanguageType language) {
        this.language = language;
    }

    public SerializeType getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    public RemotingCommandType getRemotingCommandType() {
        return remotingCommandType;
    }

    public void setRemotingCommandType(RemotingCommandType remotingCommandType) {
        this.remotingCommandType = remotingCommandType;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }


    @Override
    public String toString() {
        return "RemotingCommand [code=" + code + ", language=" + language + ", version=" + version + ", requestId=" + requestId
                + ", remark=" + remark + ", extFields=" + extFields + ", serializeType="
                + serializeType + ",remotingCommandType=" + remotingCommandType + "]";
    }

}
