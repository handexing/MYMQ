package com.my.mq.serializer.mymq;

import com.my.mq.common.spi.SpiMeta;
import com.my.mq.remoting.enums.LanguageType;
import com.my.mq.remoting.enums.RemotingCommandType;
import com.my.mq.remoting.enums.SerializeType;
import com.my.mq.remoting.protocol.RemotingCommand;
import com.my.mq.serializer.Serialization;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author: handx
 * @description: mymq序列化
 * @create 2020-10-14 14:22
 */
@SpiMeta(name = "mymq")
public class DefaultSerializable implements Serialization {

    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    public static byte[] mapSerialize(HashMap<String, String> map) {
        // keySize+key+valSize+val
        if (null == map || map.isEmpty())
            return null;

        int totalLength = 0;
        int kvLength;
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getKey() != null && entry.getValue() != null) {
                kvLength =
                    // keySize + Key
                    2 + entry.getKey().getBytes(CHARSET_UTF8).length
                        // valSize + val
                        + 4 + entry.getValue().getBytes(CHARSET_UTF8).length;
                totalLength += kvLength;
            }
        }

        ByteBuffer content = ByteBuffer.allocate(totalLength);
        byte[] key;
        byte[] val;
        it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getKey() != null && entry.getValue() != null) {
                key = entry.getKey().getBytes(CHARSET_UTF8);
                val = entry.getValue().getBytes(CHARSET_UTF8);

                content.putShort((short) key.length);
                content.put(key);

                content.putInt(val.length);
                content.put(val);
            }
        }

        return content.array();
    }

    private static int calTotalLen(int remark, int ext) {
        // int code(~32767)
        int length = 2
            // LanguageCode language
            + 1
            // int version(~32767)
            + 2
            // int opaque
            + 4
            // int flag
            + 4
            // String remark
            + 4 + remark
            // HashMap<String, String> extFields
            + 4 + ext;

        return length;
    }

    public static HashMap<String, String> mapDeserialize(byte[] bytes) {
        if (bytes == null || bytes.length <= 0)
            return null;

        HashMap<String, String> map = new HashMap<String, String>();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        short keySize;
        byte[] keyContent;
        int valSize;
        byte[] valContent;
        while (byteBuffer.hasRemaining()) {
            keySize = byteBuffer.getShort();
            keyContent = new byte[keySize];
            byteBuffer.get(keyContent);

            valSize = byteBuffer.getInt();
            valContent = new byte[valSize];
            byteBuffer.get(valContent);

            map.put(new String(keyContent, CHARSET_UTF8), new String(valContent, CHARSET_UTF8));
        }
        return map;
    }

    public static boolean isBlank(String str) {
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

    @Override
    public <T> byte[] encode(T obj) throws Exception {
        // String remark
        byte[] remarkBytes = null;
        int remarkLen = 0;
        RemotingCommand cmd = (RemotingCommand) obj;
        if (cmd.getRemark() != null && cmd.getRemark().length() > 0) {
            remarkBytes = cmd.getRemark().getBytes(CHARSET_UTF8);
            remarkLen = remarkBytes.length;
        }

        // HashMap<String, String> extFields
        byte[] extFieldsBytes = null;
        int extLen = 0;
        if (cmd.getExtFields() != null && !cmd.getExtFields().isEmpty()) {
            extFieldsBytes = mapSerialize(cmd.getExtFields());
            extLen = extFieldsBytes.length;
        }

        int totalLen = calTotalLen(remarkLen, extLen);

        ByteBuffer headerBuffer = ByteBuffer.allocate(totalLen);
        // int code(~32767)
        headerBuffer.putShort((short) cmd.getCode());
        // LanguageCode language
        headerBuffer.put(cmd.getLanguage().getType());
        // int version(~32767)
        headerBuffer.putShort((short) cmd.getVersion());
        // int requestId
        headerBuffer.putInt(cmd.getRequestId());
        // String remark
        if (remarkBytes != null) {
            headerBuffer.putInt(remarkBytes.length);
            headerBuffer.put(remarkBytes);
        } else {
            headerBuffer.putInt(0);
        }
        // HashMap<String, String> extFields;
        if (extFieldsBytes != null) {
            headerBuffer.putInt(extFieldsBytes.length);
            headerBuffer.put(extFieldsBytes);
        } else {
            headerBuffer.putInt(0);
        }
        headerBuffer.put(cmd.getRemotingCommandType().getType());
        headerBuffer.put(cmd.getRemotingCommandType().getType());

        return headerBuffer.array();
    }

    @Override
    public <T> T decode(byte[] bytes, Class<T> clazz) throws Exception {
        ByteBuffer headerBuffer = ByteBuffer.wrap(bytes);
        RemotingCommand command = RemotingCommand.builder().code(headerBuffer.getShort())
                .LanguageType(LanguageType.getByType(headerBuffer.get()))
                .serializeType(SerializeType.getByType(headerBuffer.get()))
                .remotingCommandType(RemotingCommandType.getByType(headerBuffer.get()))
                .version(headerBuffer.getShort())
                .requestId(headerBuffer.getInt()).build();

        // String remark
        int remarkLength = headerBuffer.getInt();
        if (remarkLength > 0) {
            byte[] remarkContent = new byte[remarkLength];
            headerBuffer.get(remarkContent);
            command.setRemark(new String(remarkContent, CHARSET_UTF8));
        }

        // HashMap<String, String> extFields
        int extFieldsLength = headerBuffer.getInt();
        if (extFieldsLength > 0) {
            byte[] extFieldsBytes = new byte[extFieldsLength];
            headerBuffer.get(extFieldsBytes);
            command.setExtFields(mapDeserialize(extFieldsBytes));
        }
        return (T) command;
    }
}
