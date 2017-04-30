package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/27.
 */

public class FixedLengthGetter implements IIntLengthGetter {
    private int mDataLength;

    public FixedLengthGetter(int length) {
        mDataLength = length;
    }

    @Override
    public int getLength() {
        return mDataLength;
    }
}
