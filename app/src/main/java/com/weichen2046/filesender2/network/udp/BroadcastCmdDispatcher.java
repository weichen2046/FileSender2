package com.weichen2046.filesender2.network.udp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by chenwei on 12/5/16.
 */

public class BroadcastCmdDispatcher extends Handler {

    private static final String TAG = "BroadcastCmdDispatcher";

    public BroadcastCmdDispatcher(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        UdpCmdHandler handler = UdpCmdHandler.getHandler(msg.what);
        if (handler != null) {
            BroadcastData bd = (BroadcastData) msg.obj;
            handler.handle(bd);
        } else {
            Log.w(TAG, "Unknown broadcast cmd: " + msg.what);
        }
    }
}
