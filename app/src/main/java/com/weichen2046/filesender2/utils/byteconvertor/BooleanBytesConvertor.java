package com.weichen2046.filesender2.utils.byteconvertor;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/2/7.
 */

public class BooleanBytesConvertor extends BytesConvertor {
    private boolean mData;

    private static final byte YES   = 1;
    private static final byte NO    = 0;

    public BooleanBytesConvertor(boolean data) {
        mData = data;
    }

    @Override
    public byte[] onGetBytes() {
        ByteBuffer buf = ByteBuffer.allocate(Byte.SIZE / 8);
        buf.put(mData ? YES : NO);
        markEnded();
        return buf.array();
    }
}
