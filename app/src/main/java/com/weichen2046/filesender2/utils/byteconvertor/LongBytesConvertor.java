package com.weichen2046.filesender2.utils.byteconvertor;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/2/7.
 */

public class LongBytesConvertor extends BytesConvertor {
    private long mData;

    public LongBytesConvertor(long data) {
        mData = data;
    }

    @Override
    public byte[] onGetBytes() {
        ByteBuffer buf = ByteBuffer.allocate(Long.SIZE / 8);
        buf.putLong(mData);
        markEnded();
        return buf.array();
    }
}
