package com.my.mq.remoting.netty;


import com.my.mq.remoting.protocol.RemotingCommand;

public interface RemotingResponseCallback {
    void callback(RemotingCommand response);
}
