package com.weichen2046.filesender2.utils.byteconvertor;

/**
 * Created by chenwei on 2017/2/7.
 */

public class StringBytesConvertor extends BytesConvertor {
    private String mData;

    public StringBytesConvertor(String data) {
        mData = data;
    }

    @Override
    public byte[] onGetBytes() {
        markEnded();
        return mData.getBytes();
    }
}
