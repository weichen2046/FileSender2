package com.weichen2046.filesender2.utils.byteconvertor;

/**
 * Created by chenwei on 2017/2/7.
 */

public class TransparentBytesConvertor extends BytesConvertor {
    private byte[] mData;

    public TransparentBytesConvertor(byte[] data) {
        mData = data;
    }

    @Override
    public byte[] onGetBytes() {
        markEnded();
        return mData;
    }
}
