package com.my.mq.remoting.remoting;

import com.my.mq.remoting.netty.ResponseFuture;

public interface InvokeCallback {

    void operationComplete(final ResponseFuture responseFuture);

}
