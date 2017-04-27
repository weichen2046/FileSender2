package com.weichen2046.filesender2.network.tcp.state;

import com.weichen2046.filesender2.network.tcp.state.ConsumerCallback;
import com.weichen2046.filesender2.network.tcp.state.ILengthGetter;

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
    protected ILengthGetter mLengthGetter;
    protected ConsumerCallback mCallback;

    public StateConsumer(ILengthGetter lengthGetter, ConsumerCallback callback) {
        mLengthGetter = lengthGetter;
        mCallback = callback;
    }

    public HandleState handle(byte[] buffer) {
        byte[] data = mergeData(buffer);
        int required = mLengthGetter.getRequiredLength();
        if (data == null || data.length < required) {
            return HandleState.MORE_DATA;
        }

        Object value = onHandle(data);

        // calc remains
        byte[] remains = null;
        if (data.length > required) {
            int remainLength = data.length - required;
            remains = new byte[remainLength];
            System.arraycopy(data, required, remains, 0, remainLength);
        }

        mCallback.onDataParsed(value, remains);
        return HandleState.OK;
    }

    public Object onHandle(byte[] buffer) {
        return null;
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
