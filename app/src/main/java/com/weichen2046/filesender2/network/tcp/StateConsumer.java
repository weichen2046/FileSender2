package com.weichen2046.filesender2.network.tcp;

/**
 * Created by chenwei on 2017/4/9.
 */

public class StateConsumer {
    public static final int UNSPECIFIED = -1;

    public enum HandleState {
        OK,
        FAIL,
        MORE_DATA
    }

    protected byte[] mRemains;
    protected int mRequired = UNSPECIFIED;
    protected ConsumerCallback mCallback;

    public StateConsumer(int required, ConsumerCallback callback) {
        mRequired = required;
        mCallback = callback;
    }

    public HandleState handle(byte[] buffer) {
        byte[] data = mergeData(buffer);
        if (data.length < getRequired()) {
            return HandleState.MORE_DATA;
        }

        Object value = onHandle(data);

        // TODO: calc remains
        byte[] remains = null;

        mCallback.onDataParsed(value, remains);
        return HandleState.OK;
    }

    public Object onHandle(byte[] buffer) {
        return null;
    }

    public byte[] getRemains() {
        return mRemains;
    }

    private long getRequired() {
        // TODO: refactor to return dynamic length
        return 0;
    }

    private byte[] mergeData(byte[] data) {
        if (mRemains == null) {
            return data;
        }
        if (data == null) {
            return mRemains;
        }
        byte[] buff = new byte[mRemains.length + data.length];
        System.arraycopy(mRemains, 0, buff, 0, mRemains.length);
        System.arraycopy(data, 0, buff, mRemains.length, data.length);
        return buff;
    }
}
