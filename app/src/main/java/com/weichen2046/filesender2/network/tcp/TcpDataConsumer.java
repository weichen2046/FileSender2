package com.weichen2046.filesender2.network.tcp;

import android.util.Log;

import java.util.ArrayList;


/**
 * Created by chenwei on 2017/4/9.
 */

public class TcpDataConsumer {
    private static final String TAG = "TcpDataConsumer";

    private TcpDataConsumer mInnerConsumer = null;

    private ArrayList<StateConsumer> mConsumers = new ArrayList<>();
    private int mConsumerIndex = 0;

    protected int mVersion;
    protected int mCmd;

    public StateConsumer.HandleState handle(byte[] buffer) {
        if (mInnerConsumer != null) {
            return mInnerConsumer.handle(buffer);
        } else {
            if (mConsumerIndex >= mConsumers.size()) {
                Log.d(TAG, "current index out of bounds");
                return StateConsumer.HandleState.FAIL;
            }
            StateConsumer consumer = mConsumers.get(mConsumerIndex);
            StateConsumer.HandleState res = consumer.handle(buffer);
            if (res == StateConsumer.HandleState.OK) {
                mConsumerIndex++;
            }
            return res;
        }
    }

    public void init() {
        mConsumers.add(new VersionConsumer(new ConsumerCallback() {
            @Override
            public void onDataParsed(Object value, byte[] remains) {
                mVersion = (int) value;
                Log.d(TAG, "version: " + mVersion);
            }
        }));
        mConsumers.add(new CmdConsumer(new ConsumerCallback() {
            @Override
            public void onDataParsed(Object value, byte[] remains) {
                mCmd = (int) value;
                Log.d(TAG, "cmd: " + mCmd);
                // TODO: according to cmd, create inner consumer
            }
        }));
    }
}
