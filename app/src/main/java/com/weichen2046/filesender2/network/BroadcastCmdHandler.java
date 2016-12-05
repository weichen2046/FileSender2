package com.weichen2046.filesender2.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.nio.ByteBuffer;


/**
 * Created by chenwei on 12/5/16.
 */

public class BroadcastCmdHandler extends Handler {

    private static final String TAG = "BroadcastCmdHandler";

    public BroadcastCmdHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case INetworkDefs.CMD_REPORT_PC_MONITOR_PORT:
                int port = ByteBuffer.wrap((byte[]) msg.obj).getInt();
                Log.d(TAG, "PC listen port: " + port);
                break;
            default:
                Log.w(TAG, "Unknow broadcast cmd: " + msg.what);
        }
    }
}
