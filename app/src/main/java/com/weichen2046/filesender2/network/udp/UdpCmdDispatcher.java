package com.weichen2046.filesender2.network.udp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.IServiceManagerHolder;

/**
 * Created by chenwei on 12/5/16.
 */

public class UdpCmdDispatcher extends Handler implements IServiceManagerHolder {

    private static final String TAG = "UdpCmdDispatcher";
    private IServiceManager mServiceManager;

    public UdpCmdDispatcher(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        UdpCmdHandler handler = UdpCmdHandler.getHandler(msg.what);
        if (handler != null) {
            handler.attach(mServiceManager);
            BroadcastData bd = (BroadcastData) msg.obj;
            handler.handle(bd);
        }
    }

    @Override
    public void attach(IServiceManager manager) {
        mServiceManager = manager;
    }

    @Override
    public void detach() {
        mServiceManager = null;
    }

    @Override
    public IServiceManager get() {
        return mServiceManager;
    }
}
