package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/27.
 */

public class FixedLengthGetter implements ILengthGetter {
    private int mLength;

    public FixedLengthGetter(int length) {
        mLength = length;
    }

    @Override
    public int getRequiredLength() {
        return mLength;
    }
}
