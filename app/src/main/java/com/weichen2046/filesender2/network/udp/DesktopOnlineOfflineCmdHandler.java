package com.weichen2046.filesender2.network.udp;

import android.os.RemoteException;
import android.util.Log;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.service.IRemoteDeviceDiscoverer;
import com.weichen2046.filesender2.service.IRemoteDevicesManager;
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
    public boolean handle(BroadcastData data) {
        if (mCmd == INetworkDefs.CMD_R_DESKTOP_ONLINE) {
            handleDesktopOnline(data);
        } else if (mCmd == INetworkDefs.CMD_R_DESKTOP_OFFLINE) {
            handleDesktopOffline(data);
        }
        return true;
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
        String desktopAddress = data.addr.getHostAddress();
        Log.d(TAG, "desktop offline, desktop addr: " + desktopAddress);

        ByteBuffer buffer = ByteBuffer.wrap(data.data);
        // 1 bytes has token indicator
        boolean hasToken = buffer.get() == 1;
        if (!hasToken) {
            // 4 bytes udp port
            int udpPort = buffer.getInt();
            Log.d(TAG, "desktop offline broadcast, udpPort: " + udpPort);
            deleteDesktopByPort(desktopAddress, udpPort);
        } else {
            // 4 bytes token length
            int tokenLength = buffer.getInt();
            // read token
            byte[] tokenBytes = new byte[tokenLength];
            buffer.get(tokenBytes);
            String token = new String(tokenBytes);
            Log.d(TAG, "desktop offline, access token: " + token);
            deleteDesktopByToken(desktopAddress, token);
        }
    }

    private void sayHello(String address, int port) {
        IServiceManager manager = getServiceManager();
        try {
            IRemoteDeviceDiscoverer discoverer = IRemoteDeviceDiscoverer.Stub.asInterface(
                    manager.getService(ServiceManager.SERVICE_DEVICE_DISCOVERER));
            discoverer.sayHello(address, port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void deleteDesktopByPort(String address, int port) {
        IServiceManager manager = getServiceManager();
        try {
            IRemoteDevicesManager devicesManager = IRemoteDevicesManager.Stub.asInterface(
                    manager.getService(ServiceManager.SERVICE_DEVICES_MANAGER));
            boolean res = devicesManager.deleteDesktopByPort(address, port);
            Log.d(TAG, "delete desktop " + address + " " + (res ? "ok" : "fail"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void deleteDesktopByToken(String address, String authToken) {
        IServiceManager manager = getServiceManager();
        try {
            IRemoteDevicesManager devicesManager = IRemoteDevicesManager.Stub.asInterface(
                    manager.getService(ServiceManager.SERVICE_DEVICES_MANAGER));
            boolean res = devicesManager.deleteDesktopByAuthToken(address, authToken);
            Log.d(TAG, "delete desktop " + address + " " + (res ? "ok" : "fail"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
