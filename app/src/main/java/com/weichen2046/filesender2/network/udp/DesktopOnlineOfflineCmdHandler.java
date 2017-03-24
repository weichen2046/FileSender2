package com.weichen2046.filesender2.network.udp;

import android.os.RemoteException;
import android.util.Log;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.service.IPCDiscoverer;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManager;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/1/31.
 */

public class DesktopOnlineOfflineCmdHandler extends UdpCmdHandler {
    public DesktopOnlineOfflineCmdHandler(int cmd) {
        super(cmd);
    }

    @Override
    public void handle(BroadcastData data) {
        if (mCmd == INetworkDefs.CMD_DESKTOP_ONLINE) {
            handleDesktopOnline(data);
        } else if (mCmd == INetworkDefs.CMD_DESKTOP_OFFLINE) {
            handleDesktopOffline(data);
        }
    }

    private void handleDesktopOnline(BroadcastData data) {
        if (data.data == null) {
            Log.w(TAG, "handle desktop online but data is null");
            return;
        }
        // read token length
        ByteBuffer buffer = ByteBuffer.wrap(data.data);
        int tokenLength = buffer.getInt();
        // read token
        byte[] tokenBytes = new byte[tokenLength];
        buffer.get(tokenBytes);
        String token = new String(tokenBytes);
        // read udp port
        int udpPort = buffer.getInt();
        Log.d(TAG, "desktop online, udp port: " + udpPort + ", temp token: " + token);
        sayHello(data.addr.getHostAddress(), udpPort);
    }

    private void handleDesktopOffline(BroadcastData data) {
        Log.d(TAG, "desktop offline, desktop addr: " + data.addr.getHostAddress());
        // TODO: delete desktop from DesktopManager
    }

    private void sayHello(String address, int port) {
        IServiceManager manager = get();
        try {
            IPCDiscoverer discoverer = IPCDiscoverer.Stub.asInterface(
                    manager.getService(ServiceManager.SERVICE_PC_DISCOVERER));
            discoverer.sayHello(address, port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
