package com.my.mq.remoting.netty;

import com.my.mq.common.spi.ExtensionLoader;
import com.my.mq.remoting.common.RemotingHelper;
import com.my.mq.remoting.common.RemotingUtil;
import com.my.mq.remoting.enums.SerializeType;
import com.my.mq.remoting.protocol.RemotingCommand;
import com.my.mq.serializer.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@ChannelHandler.Sharable
public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {

    private static Logger log = LoggerFactory.getLogger(NettyEncoder.class);

    @Override
    public void encode(ChannelHandlerContext ctx, RemotingCommand remotingCommand, ByteBuf out) {
        try {
            ByteBuffer header = encodeHeader(remotingCommand);
            out.writeBytes(header);
            byte[] body = remotingCommand.getBody();
            if (body != null) {
                out.writeBytes(body);
            }
        } catch (Exception e) {
            log.error("encode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            if (remotingCommand != null) {
                log.error(remotingCommand.toString());
            }
            RemotingUtil.closeChannel(ctx.channel());
        }
    }

    public ByteBuffer encodeHeader(RemotingCommand remotingCommand) throws Exception {
        return encodeHeader(remotingCommand.getBody() != null ? remotingCommand.getBody().length : 0, remotingCommand);
    }

    public ByteBuffer encodeHeader(final int bodyLength, final RemotingCommand remotingCommand) throws Exception {
        // 1> header length size
        int length = 4;

        // 2> header data length
        byte[] headerData;
        headerData = this.headerEncode(remotingCommand);

        length += headerData.length;

        // 3> body data length
        length += bodyLength;

        ByteBuffer result = ByteBuffer.allocate(4 + length - bodyLength);

        // length
        result.putInt(length);

        // header length
        result.put(markProtocolType(headerData.length, remotingCommand.getSerializeType()));

        // header data
        result.put(headerData);

        result.flip();

        return result;
    }

    private byte[] headerEncode(RemotingCommand remotingCommand) throws Exception {
        Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(remotingCommand.getSerializeType().getName());
        return serialization.encode(remotingCommand);
    }

    public static byte[] markProtocolType(int source, SerializeType type) {
        byte[] result = new byte[4];

        result[0] = type.getCode();
        result[1] = (byte) ((source >> 16) & 0xFF);
        result[2] = (byte) ((source >> 8) & 0xFF);
        result[3] = (byte) (source & 0xFF);
        return result;
    }

}
