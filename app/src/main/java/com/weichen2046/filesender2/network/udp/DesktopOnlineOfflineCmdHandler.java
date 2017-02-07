package com.weichen2046.filesender2.network.udp;

import android.util.Log;

import com.weichen2046.filesender2.network.DesktopMachine;
import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.network.DesktopManager;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/1/31.
 */

public class DesktopOnlineOfflineCmdHandler extends UdpCmdHandler {

    private DesktopManager mManager = DesktopManager.getInstance();

    public DesktopOnlineOfflineCmdHandler(int cmd) {
        super(cmd);
    }

    @Override
    public void handle(BroadcastData data) {
        if (mCmd == INetworkDefs.CMD_PC_ONLINE) {
            handleDesktopOnline(data);
        } else if (mCmd == INetworkDefs.CMD_PC_OFFLINE) {
            handleDesktopOffline(data);
        }
    }

    private void handleDesktopOnline(BroadcastData data) {
        if (data.data == null) {
            Log.w(TAG, "handle desktop online but data is null");
            return;
        }
        int port = ByteBuffer.wrap(data.data).getInt();
        Log.d(TAG, "desktop online, pc tcp listen port: " + port);
        DesktopMachine newDesktop = new DesktopMachine(data.addr, port);
        DesktopMachine oldDesktop = mManager.add(newDesktop);
        if (newDesktop == oldDesktop) {
            // TODO: query desktop details
        }
    }

    private void handleDesktopOffline(BroadcastData data) {
        Log.d(TAG, "desktop offline, desktop addr: " + data.addr);
        mManager.remove(data.addr);
    }
}
