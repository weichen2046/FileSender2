package com.weichen2046.filesender2.network.tcp;

import android.util.Log;

import com.weichen2046.filesender2.network.tcp.state.AuthTokenConsumer;
import com.weichen2046.filesender2.network.tcp.state.AuthTokenLengthConsumer;
import com.weichen2046.filesender2.network.tcp.state.ConsumerCallback;
import com.weichen2046.filesender2.network.tcp.state.DynamicLengthGetter;

/**
 * Created by chenwei on 2017/4/27.
 */

public class AuthTcpDataConsumer extends TcpDataConsumer {
    protected int mTokenLength;
    protected String mToken;

    @Override
    public void onInitStates() {
        addStateConsumer(new AuthTokenLengthConsumer(new ConsumerCallback() {
            @Override
            public void onDataParsed(Object value, byte[] remains) {
                mTokenLength = (int) value;
                Log.d(TAG, "token length: " + mTokenLength);
                mRemains = remains;
            }
        }));
        addStateConsumer(
                new AuthTokenConsumer(new DynamicLengthGetter(this, "getTokenLength"),
                        new ConsumerCallback() {
            @Override
            public void onDataParsed(Object value, byte[] remains) {
                mToken = value.toString();
                Log.d(TAG, "token: " + mToken);
                mRemains = remains;
            }
        }));
    }

    public int getTokenLength() {
        return mTokenLength;
    }
}
