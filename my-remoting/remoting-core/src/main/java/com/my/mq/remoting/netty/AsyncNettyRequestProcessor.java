package com.my.mq.remoting.netty;

import com.my.mq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

public abstract class AsyncNettyRequestProcessor implements NettyRequestProcessor {

    public void asyncProcessRequest(ChannelHandlerContext ctx, RemotingCommand request, RemotingResponseCallback responseCallback) throws Exception {
        RemotingCommand response = processRequest(ctx, request);
        responseCallback.callback(response);
    }
}
