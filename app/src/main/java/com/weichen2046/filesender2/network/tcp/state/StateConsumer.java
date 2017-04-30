package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/9.
 */

public abstract class StateConsumer {
    protected static final String TAG = "StateConsumer";

    public enum HandleState {
        OK,
        FAIL,
        MORE_DATA
    }

    private boolean mInitialized = false;

    protected byte[] mRemains;
    protected StateConsumerCallback mCallback;
    protected IIntLengthGetter mLengthGetter;

    public StateConsumer(IIntLengthGetter lengthGetter, StateConsumerCallback callback) {
        mLengthGetter = lengthGetter;
        mCallback = callback;
    }

    public void initializedIfNeeded() {
        if (!isInitialized()) {
            begin();
        }
    }

    public void destroyIfNeeded() {
        if (isInitialized()) {
            end();
        }
    }

    protected void begin() {
        onBegin();
        mInitialized = true;
    }

    protected void end() {
        onEnd();
    }

    protected void reset() {
        onReset();
    }

    public HandleState handle(byte[] buffer) {
        mRemains = buffer;
        IIntLengthGetter lengthGetter = getLengthGetter();
        int required = lengthGetter.getLength();

        byte[] data = buffer;
        if (required != IIntLengthGetter.UNKNOWN_LENGTH
                && (data == null || data.length < required)) {
            return HandleState.MORE_DATA;
        }

        Object value = onHandle(data);

        // calc remains
        byte[] remains = null;
        if (required == IIntLengthGetter.UNKNOWN_LENGTH) {
            remains = mRemains;
        } else if (data.length > required) {
            int remainLength = data.length - required;
            remains = new byte[remainLength];
            System.arraycopy(data, required, remains, 0, remainLength);
        }
        mRemains = remains;

        StateConsumerCallback callback = getConsumerCallback();
        if (callback != null) {
            callback.onDataParsed(value, mRemains);
        }

        return getHandleState();
    }

    public byte[] getAndResetRemains() {
        byte[] remains = mRemains;
        mRemains = null;
        return remains;
    }

    protected boolean isInitialized() {
        return mInitialized;
    }

    protected abstract Object onHandle(byte[] buffer);
    protected void onBegin() {}
    protected void onEnd() {}
    protected void onReset() {}

    protected IIntLengthGetter getLengthGetter() {
        return mLengthGetter;
    }

    protected StateConsumerCallback getConsumerCallback() {
        return mCallback;
    }

    protected HandleState getHandleState() {
        return HandleState.OK;
    }

    /**
     * Created by chenwei on 2017/4/9.
     */

    public interface StateConsumerCallback {
        void onDataParsed(Object value, byte[] remains);
    }
}
