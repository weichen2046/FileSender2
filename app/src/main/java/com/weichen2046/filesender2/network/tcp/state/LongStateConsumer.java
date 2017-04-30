package com.weichen2046.filesender2.network.tcp.state;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/4/30.
 */

public class LongStateConsumer extends StateConsumer {
    private static final int LONG_LENGTH = 8;

    public LongStateConsumer(StateConsumerCallback callback) {
        super(new FixedLengthGetter(LONG_LENGTH), callback);
    }

    @Override
    protected Object onHandle(byte[] buffer) {
        ByteBuffer bb = ByteBuffer.wrap(buffer, 0, LONG_LENGTH);
        return bb.getLong();
    }
}
