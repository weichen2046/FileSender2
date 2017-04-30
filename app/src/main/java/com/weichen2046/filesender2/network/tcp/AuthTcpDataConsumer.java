package com.weichen2046.filesender2.network.tcp;

import android.util.Log;

import com.weichen2046.filesender2.network.tcp.state.StateConsumer;
import com.weichen2046.filesender2.network.tcp.state.DynamicIntLengthGetter;
import com.weichen2046.filesender2.network.tcp.state.IntStateConsumer;
import com.weichen2046.filesender2.network.tcp.state.StringStateConsumer;

/**
 * Created by chenwei on 2017/4/27.
 */

public class AuthTcpDataConsumer extends TcpDataConsumer {
    protected int mTokenLength;
    protected String mToken;

    @Override
    public void onInitStates() {
        addStateConsumer(new IntStateConsumer(new StateConsumer.StateConsumerCallback() {
            @Override
            public void onDataParsed(Object value, byte[] remains) {
                mTokenLength = (int) value;
                Log.d(TAG, "token length: " + mTokenLength);
            }
        }));
        addStateConsumer(
                new StringStateConsumer(new DynamicIntLengthGetter(this, "getTokenLength"),
                        new StateConsumer.StateConsumerCallback() {
            @Override
            public void onDataParsed(Object value, byte[] remains) {
                mToken = value.toString();
                Log.d(TAG, "token: " + mToken);
            }
        }));
    }

    public int getTokenLength() {
        return mTokenLength;
    }
}
