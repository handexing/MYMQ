package com.my.mq.remoting;

import com.my.mq.remoting.exception.RemotingConnectException;
import com.my.mq.remoting.exception.RemotingSendRequestException;
import com.my.mq.remoting.exception.RemotingTimeoutException;
import com.my.mq.remoting.exception.RemotingTooMuchRequestException;
import com.my.mq.remoting.netty.NettyRequestProcessor;
import com.my.mq.remoting.protocol.RemotingCommand;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 *
 */
public interface RemotingClient extends RemotingService {

    void updateNameServerAddressList(final List<String> addrs);

    List<String> getNameServerAddressList();

    RemotingCommand invokeSync(final String addr, final RemotingCommand request, final long timeoutMillis) throws InterruptedException, RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException;

    void invokeAsync(final String addr, final RemotingCommand request, final long timeoutMillis, final InvokeCallback invokeCallback) throws InterruptedException, RemotingConnectException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    void invokeOneway(final String addr, final RemotingCommand request, final long timeoutMillis) throws InterruptedException, RemotingConnectException, RemotingTooMuchRequestException,
            RemotingTimeoutException, RemotingSendRequestException;

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor, final ExecutorService executor);

    void setCallbackExecutor(final ExecutorService callbackExecutor);

    ExecutorService getCallbackExecutor();

    boolean isChannelWritable(final String addr);
}
