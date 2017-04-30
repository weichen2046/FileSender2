package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/27.
 */

public class StringStateConsumer extends StateConsumer {
    public StringStateConsumer(IIntLengthGetter lengthGetter, StateConsumerCallback callback) {
        super(lengthGetter, callback);
    }

    @Override
    public Object onHandle(byte[] buffer) {
        IIntLengthGetter lengthGetter = getLengthGetter();
        return new String(buffer, 0, lengthGetter.getLength());
    }
}
