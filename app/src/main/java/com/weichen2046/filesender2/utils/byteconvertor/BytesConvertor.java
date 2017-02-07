package com.weichen2046.filesender2.utils.byteconvertor;

/**
 * Created by chenwei on 2017/2/7.
 */

public abstract class BytesConvertor {
    private boolean mEnded = false;

    public final byte[] getBytes() {
        if (mEnded) {
            return null;
        }
        return onGetBytes();
    }

    public final void markEnded() {
        mEnded = true;
        destroy();
    }

    public void init() {
    }

    public void destroy() {
    }

    public abstract byte[] onGetBytes();
}
