package com.weichen2046.filesender2.network.tcp.state;

import com.weichen2046.filesender2.network.tcp.state.ConsumerCallback;
import com.weichen2046.filesender2.network.tcp.state.ILengthGetter;
import com.weichen2046.filesender2.network.tcp.state.StateConsumer;

/**
 * Created by chenwei on 2017/4/27.
 */

public class StringConsumer extends StateConsumer {
    public StringConsumer(ILengthGetter lengthGetter, ConsumerCallback callback) {
        super(lengthGetter, callback);
    }

    @Override
    public Object onHandle(byte[] buffer) {
        return new String(buffer, 0, mLengthGetter.getRequiredLength());
    }
}
