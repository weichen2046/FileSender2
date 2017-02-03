package com.weichen2046.filesender2.network.udp;

import android.util.Log;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.network.Pc;
import com.weichen2046.filesender2.network.PcManager;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/1/31.
 */

public class PcOnlineOfflineCmdHandler extends UdpCmdHandler {

    private PcManager mManager = PcManager.getInstance();

    public PcOnlineOfflineCmdHandler(int cmd) {
        super(cmd);
    }

    @Override
    public void handle(BroadcastData data) {
        if (mCmd == INetworkDefs.CMD_PC_ONLINE) {
            handlePcOnline(data);
        } else if (mCmd == INetworkDefs.CMD_PC_OFFLINE) {
            handlePcOffline(data);
        }
    }

    private void handlePcOnline(BroadcastData data) {
        if (data.data == null) {
            Log.w(TAG, "handle pc online but data is null");
            return;
        }
        int port = ByteBuffer.wrap(data.data).getInt();
        Log.d(TAG, "desktop online, pc tcp listen port: " + port);
        mManager.add(new Pc(data.addr, port));
    }

    private void handlePcOffline(BroadcastData data) {
        Log.d(TAG, "desktop offline, desktop addr: " + data.addr);
        mManager.remove(data.addr);
    }
}
