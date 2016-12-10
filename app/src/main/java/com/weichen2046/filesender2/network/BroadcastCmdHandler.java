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
        BroadcastData bd = (BroadcastData) msg.obj;
        PcManager manager = PcManager.getInstance();
        switch (msg.what) {
            case INetworkDefs.CMD_PC_ONLINE:
                if (null == bd.data) {
                    Log.w(TAG, "Get report pc monitor, but data is null");
                    return;
                }
                int port = ByteBuffer.wrap(bd.data).getInt();
                Log.d(TAG, "PC listen port: " + port);
                manager.add(new PcData(bd.addr, port));
                break;
            case INetworkDefs.CMD_PC_OFFLINE:
                manager.remove(bd.addr);
                break;
            default:
                Log.w(TAG, "Unknow broadcast cmd: " + msg.what);
        }
    }
}
