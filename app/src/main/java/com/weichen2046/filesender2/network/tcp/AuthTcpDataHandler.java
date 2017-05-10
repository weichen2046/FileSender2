package com.weichen2046.filesender2.network.tcp;

import android.os.RemoteException;
import android.util.Log;

import com.weichen2046.filesender2.network.tcp.state.StateConsumer;
import com.weichen2046.filesender2.network.tcp.state.DynamicIntLengthGetter;
import com.weichen2046.filesender2.network.tcp.state.IntStateConsumer;
import com.weichen2046.filesender2.network.tcp.state.StringStateConsumer;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.IDesktopManager;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManager;

/**
 * Created by chenwei on 2017/4/27.
 */

public class AuthTcpDataHandler extends TcpDataHandler {
    protected int mTokenLength;
    protected String mToken;
    protected Desktop mAuthedDevice;

    @Override
    public void onInitStates() {
        addStateConsumer(new IntStateConsumer(new StateConsumer.StateConsumerCallback() {
            @Override
            public boolean onDataParsed(Object value, byte[] remains) {
                mTokenLength = (int) value;
                Log.d(TAG, "token length: " + mTokenLength);
                return true;
            }
        }));
        addStateConsumer(
                new StringStateConsumer(new DynamicIntLengthGetter(this, "getTokenLength"),
                        new StateConsumer.StateConsumerCallback() {
            @Override
            public boolean onDataParsed(Object value, byte[] remains) {
                mToken = value.toString();
                Log.d(TAG, "token: " + mToken);
                IServiceManager manager = getServiceManager();
                try {
                    IDesktopManager desktopManager = IDesktopManager.Stub.asInterface(
                            manager.getService(ServiceManager.SERVICE_DESKTOP_MANAGER));
                    mAuthedDevice = desktopManager.findDesktopByAuthToken(mRemoteHost, mToken);
                    Log.d(TAG, "authed device: " + mAuthedDevice);
                    if (mAuthedDevice == null) {
                        return false;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }));
    }

    public int getTokenLength() {
        return mTokenLength;
    }
}
