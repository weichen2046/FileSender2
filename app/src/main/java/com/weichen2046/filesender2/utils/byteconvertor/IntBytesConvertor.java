package com.weichen2046.filesender2.utils.byteconvertor;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/2/7.
 */

public class IntBytesConvertor extends BytesConvertor {
    private int mData;

    public IntBytesConvertor(int data) {
        mData = data;
    }

    @Override
    public byte[] onGetBytes() {
        ByteBuffer buf = ByteBuffer.allocate(Integer.SIZE / 8);
        buf.putInt(mData);
        markEnded();
        return buf.array();
    }
}
