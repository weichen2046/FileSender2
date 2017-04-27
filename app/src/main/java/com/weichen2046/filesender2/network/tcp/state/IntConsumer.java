package com.weichen2046.filesender2.network.tcp.state;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/4/9.
 */

public class IntConsumer extends StateConsumer {
    private static final int LENGTH = 4;

    public IntConsumer(ConsumerCallback callback) {
        super(new FixedLengthGetter(LENGTH), callback);
    }

    @Override
    public Object onHandle(byte[] buffer) {
        ByteBuffer bb = ByteBuffer.wrap(buffer, 0, LENGTH);
        return bb.getInt();
    }
}
