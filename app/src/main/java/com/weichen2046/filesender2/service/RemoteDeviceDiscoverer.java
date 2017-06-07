package com.weichen2046.filesender2.service;

import android.os.RemoteException;
import android.util.Log;

import com.weichen2046.filesender2.MyApplication;
import com.weichen2046.filesender2.networklib.TokenHelper;

/**
 * Created by chenwei on 12/4/16.
 */

public class RemoteDeviceDiscoverer extends IRemoteDeviceDiscoverer.Stub {

    private static final String TAG = "RemoteDeviceDiscoverer";

    private String mTempAccessToken;

    @Override
    public void sayHello(final String address, final int port) throws RemoteException {
        SocketTaskService.sayHello(MyApplication.getInstance(), address, port, getTempToken());
    }

    @Override
    public boolean checkTempAccessToken(String token) throws RemoteException {
        return mTempAccessToken.equals(token);
    }

    private String getTempToken() {
        if (null == mTempAccessToken) {
            mTempAccessToken = TokenHelper.generateTempToken();
        }
        Log.d(TAG, "temp access token: " + mTempAccessToken);
        return mTempAccessToken;
    }
}
