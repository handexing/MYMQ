package com.my.mq.remoting.netty;

import com.my.mq.common.spi.ExtensionLoader;
import com.my.mq.remoting.common.RemotingUtil;
import com.my.mq.remoting.enums.SerializeType;
import com.my.mq.remoting.protocol.RemotingCommand;
import com.my.mq.serializer.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteBuffer;

public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    private static final int FRAME_MAX_LENGTH =
        Integer.parseInt(System.getProperty("com.rocketmq.remoting.frameMaxLength", "16777216"));

    public NettyDecoder() {
        super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }
            ByteBuffer byteBuffer = frame.nioBuffer();
            return decode(byteBuffer);
        } catch (Exception e) {
//            log.error("decode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            RemotingUtil.closeChannel(ctx.channel());
        } finally {
            if (null != frame) {
                frame.release();
            }
        }
        return null;
    }

    public static RemotingCommand decode(final byte[] array) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        return decode(byteBuffer);
    }

    public static RemotingCommand decode(final ByteBuffer byteBuffer) throws Exception {
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
        cmd.setBody(bodyData);
        return cmd;
    }

    public static int getHeaderLength(int length) {
        return length & 0xFFFFFF;
    }

    private static RemotingCommand headerDecode(byte[] headerData, SerializeType type) throws Exception {
        Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(type.getName());
        RemotingCommand cmd = serialization.decode(headerData, RemotingCommand.class);
        cmd.setSerializeType(type);
        return cmd;
        /*switch (type) {
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
        return null;*/
    }

    public static SerializeType getProtocolType(int source) {
        return SerializeType.getByType((byte) ((source >> 24) & 0xFF));
    }
}
