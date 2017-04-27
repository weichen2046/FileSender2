package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/27.
 */

public class AuthTokenConsumer extends StringConsumer {
    public AuthTokenConsumer(ILengthGetter lengthGetter, ConsumerCallback callback) {
        super(lengthGetter, callback);
    }
}
