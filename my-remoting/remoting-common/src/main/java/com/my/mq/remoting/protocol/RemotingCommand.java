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
