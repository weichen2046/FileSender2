package com.weichen2046.filesender2.network.tcp.state;

import java.util.ArrayList;

/**
 * Created by chenwei on 2017/4/29.
 */

public class GroupStateConsumer extends StateConsumer {
    private ArrayList<StateConsumer> mConsumers = new ArrayList<>();
    private int mCurrentIndex = 0;

    public GroupStateConsumer() {
        super(new UnknownIntLengthGetter(), null);
    }

    public void addConsumer(StateConsumer consumer) {
        mConsumers.add(consumer);
    }

    @Override
    protected Object onHandle(byte[] buffer) {
        HandleState res = HandleState.OK;
        mRemains = buffer;
        while (res == HandleState.OK && mCurrentIndex < mConsumers.size()) {
            byte[] data = mergeData(null);
            StateConsumer consumer = mConsumers.get(mCurrentIndex);
            consumer.initializedIfNeeded();
            res = consumer.handle(data);
            mRemains = consumer.getAndResetRemains();
            if (res == HandleState.OK) {
                consumer.destroyIfNeeded();
                mCurrentIndex++;
            }
        }
        return null;
    }

    @Override
    protected HandleState getHandleState() {
        if (mCurrentIndex == mConsumers.size()) {
            return HandleState.OK;
        }
        return HandleState.MORE_DATA;
    }

    @Override
    protected void onReset() {
        super.onReset();
        mCurrentIndex = 0;
        for(StateConsumer consumer : mConsumers) {
            consumer.reset();
        }
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
