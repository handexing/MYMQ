package com.my.mq.remoting.client;

import com.handx.netty.client.protocol.Result;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: handx
 * @description: TODO
 * @create 2020-09-28 14:40
 */
public class ClientHandler extends SimpleChannelInboundHandler<Result> {

    //处理服务端返回的数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Result response) throws Exception {
        System.out.println("接受到server响应数据: " + response.toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


}
