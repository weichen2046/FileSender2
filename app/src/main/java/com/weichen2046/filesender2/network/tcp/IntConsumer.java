package com.weichen2046.filesender2.network.tcp;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/4/9.
 */

public class IntConsumer extends StateConsumer {
    public IntConsumer(ConsumerCallback callback) {
        super(4, callback);
    }

    @Override
    public Object onHandle(byte[] buffer) {
        ByteBuffer bb = ByteBuffer.wrap(buffer, 0, 4);
        return bb.getInt();
    }
}
