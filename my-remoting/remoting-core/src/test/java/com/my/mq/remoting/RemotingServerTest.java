package com.my.mq.remoting;

import com.my.mq.remoting.enums.RemotingCommandType;
import com.my.mq.remoting.exception.RemotingConnectException;
import com.my.mq.remoting.exception.RemotingSendRequestException;
import com.my.mq.remoting.exception.RemotingTimeoutException;
import com.my.mq.remoting.exception.RemotingTooMuchRequestException;
import com.my.mq.remoting.netty.*;
import com.my.mq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

public class RemotingServerTest {

    private static Logger logger = LoggerFactory.getLogger(RemotingServerTest.class);

    private static RemotingServer remotingServer;
    private static RemotingClient remotingClient;

    public static RemotingServer createRemotingServer() throws InterruptedException {
        NettyServerConfig config = new NettyServerConfig();
        RemotingServer remotingServer = new NettyRemotingServer(config);
        remotingServer.registerDefaultProcessor(new AsyncNettyRequestProcessor() {
            @Override
            public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
                request.setRemark("Hi " + ctx.channel().remoteAddress());
                return request;
            }

            @Override
            public boolean rejectRequest() {
                return false;
            }
        }, Executors.newCachedThreadPool());

//        remotingServer.registerProcessor(0, new AsyncNettyRequestProcessor() {
//            @Override
//            public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
//                request.setRemark("Hi " + ctx.channel().remoteAddress());
//                return request;
//            }
//            @Override
//            public boolean rejectRequest() {
//                return false;
//            }
//        }, Executors.newCachedThreadPool());
        remotingServer.start();
        return remotingServer;
    }

    public static RemotingClient createRemotingClient() {
        return createRemotingClient(new NettyClientConfig());
    }

    public static RemotingClient createRemotingClient(NettyClientConfig nettyClientConfig) {
        RemotingClient client = new NettyRemotingClient(nettyClientConfig);
        client.start();
        return client;
    }

    @BeforeClass
    public static void setup() throws InterruptedException {
        remotingServer = createRemotingServer();
        remotingClient = createRemotingClient();
    }

    @AfterClass
    public static void destroy() {
        remotingClient.shutdown();
        remotingServer.shutdown();
    }

    @Test
    public void testInvokeSync() throws InterruptedException, RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException {
        for (int i = 0; i < 100; i++) {
            RemotingCommand request = RemotingCommand.builder().code(0)
                    .remotingCommandType(RemotingCommandType.REQUEST_COMMAND)
                    .body("this is test".getBytes())
                    .addExtFields("name", "小强")
                    .addExtFields("age", "20").build();
            long start = System.currentTimeMillis();
            RemotingCommand response = remotingClient.invokeSync("localhost:8888", request, 1000 * 3);
            logger.info(response.toString() + "::" + (System.currentTimeMillis() - start));
        }
    }

    @Test
    public void testInvokeOneway() throws InterruptedException, RemotingConnectException,
            RemotingTimeoutException, RemotingTooMuchRequestException, RemotingSendRequestException {

        RemotingCommand request = RemotingCommand.builder().code(0)
                .remotingCommandType(RemotingCommandType.REQUEST_ONE_WAY_COMMAND)
                .body("this is test".getBytes())
                .addExtFields("name", "小强")
                .addExtFields("age", "20").build();
        remotingClient.invokeOneway("localhost:8888", request, 1000 * 3);
    }

    @Test
    public void testInvokeAsync() throws InterruptedException, RemotingConnectException,
            RemotingTimeoutException, RemotingTooMuchRequestException, RemotingSendRequestException {
        long start = System.currentTimeMillis();
        RemotingCommand request = RemotingCommand.builder().code(0)
                .remotingCommandType(RemotingCommandType.REQUEST_COMMAND)
                .body("this is test".getBytes())
                .addExtFields("name", "小强")
                .addExtFields("age", "20").build();
        for (int i = 0; i < 100; i++) {
            remotingClient.invokeAsync("localhost:8888", request, 1000 * 3, new InvokeCallback() {
                @Override
                public void operationComplete(ResponseFuture responseFuture) {
                    System.out.println(responseFuture.getResponseCommand().toString() + "::" + (System.currentTimeMillis() - start));
                }
            });
        }
    }
}


