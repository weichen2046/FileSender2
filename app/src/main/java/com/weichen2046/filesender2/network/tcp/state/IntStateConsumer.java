package com.weichen2046.filesender2.network.tcp.state;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/4/9.
 */

public class IntStateConsumer extends StateConsumer {
    private static final int INT_LENGTH = 4;

    public IntStateConsumer(StateConsumerCallback callback) {
        super(new FixedLengthGetter(INT_LENGTH), callback);
    }

    @Override
    public Object onHandle(byte[] buffer) {
        ByteBuffer bb = ByteBuffer.wrap(buffer, 0, INT_LENGTH);
        return bb.getInt();
    }
}
