package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/29.
 */

public class NthStateConsumer extends StateConsumer {
    private static final int UNKNOWN_N = -1;
    private int mTargetN = UNKNOWN_N;
    private int mN = 0;

    private IIntLengthGetter mTargetNGetter;
    private StateConsumer mInnerConsumer;
    private INthChangeListener mNthChangeListener;

    public interface INthChangeListener {
        void onNthChanged(int n);
    }

    public NthStateConsumer(IIntLengthGetter targetNGetter, StateConsumer innerConsumer, INthChangeListener listener) {
        super(new UnknownIntLengthGetter(), null);
        mTargetNGetter = targetNGetter;
        mInnerConsumer = innerConsumer;
        mNthChangeListener = listener;
    }

    @Override
    public Object onHandle(byte[] buffer) {
        HandleState res = HandleState.OK;
        mRemains = buffer;
        while (res == HandleState.OK && mN < mTargetN) {
            byte[] data = mergeData(null);
            res = mInnerConsumer.handle(data);
            mRemains = mInnerConsumer.mRemains;
            if (res == HandleState.OK) {
                mN++;
                mInnerConsumer.reset();
                mNthChangeListener.onNthChanged(mN);
            }
        }
        return null;
    }

    @Override
    protected HandleState getHandleState() {
        if (mN != mTargetN) {
            return HandleState.MORE_DATA;
        }
        return super.getHandleState();
    }

    @Override
    protected void onBegin() {
        super.onBegin();
        mTargetN = mTargetNGetter.getLength();
        mNthChangeListener.onNthChanged(mN);
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
