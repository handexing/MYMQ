package com.my.mq.remoting.server;

import com.my.mq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author: handx
 * @description: TODO
 * @create 2020-09-28 14:37
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    //接受client发送的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RemotingCommand request = (RemotingCommand) msg;
        System.out.println("接收到客户端信息:" + request.toString());
        //返回的数据结构
//        ctx.writeAndFlush(response);
    }

    //通知处理器最后的channelRead()是当前批处理中的最后一条消息时调用
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端接收数据完毕..");
        ctx.flush();
    }

    //读操作时捕获到异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    //客户端去和服务端连接成功时触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush("hello client");
    }

}
