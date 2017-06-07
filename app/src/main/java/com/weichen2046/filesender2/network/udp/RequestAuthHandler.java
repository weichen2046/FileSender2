package com.weichen2046.filesender2.network.udp;

import android.os.RemoteException;
import android.util.Log;

import com.weichen2046.filesender2.MyApplication;
import com.weichen2046.filesender2.network.UnsupportedRemoteDeviceException;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.IRemoteDeviceDiscoverer;
import com.weichen2046.filesender2.service.IDesktopManager;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.RemoteDevice;
import com.weichen2046.filesender2.service.ServiceManager;
import com.weichen2046.filesender2.ui.NotificationHelper;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/3/18.
 */

public class RequestAuthHandler extends UdpCmdHandler {
    public RequestAuthHandler(int cmd) {
        super(cmd);
    }

    @Override
    public boolean handle(BroadcastData data) {
        ByteBuffer buffer = ByteBuffer.wrap(data.data);
        // read temp auth token length
        int tokenLength = buffer.getInt();
        byte[] tempTokenBytes = new byte[tokenLength];
        // read temp auth token
        buffer.get(tempTokenBytes);
        String tempAuthToken = new String(tempTokenBytes);
        // read remote device type
        int deviceType = buffer.getInt();
        if (!RemoteDevice.isValidDeviceType(deviceType)) {
            throw new UnsupportedRemoteDeviceException(deviceType);
        }
        // read token length
        tokenLength = buffer.getInt();
        // read remote device access token
        byte[] tokenBytes = new byte[tokenLength];
        buffer.get(tokenBytes);
        String token = new String(tokenBytes);
        // read remote device udp port
        int udpPort = buffer.getInt();

        Log.d(TAG, "hand request auth, temp auth token: " + tempAuthToken
                + ", device type: " + deviceType
                + ", token: " + token + ", udp port: " + udpPort);

        // authenticate use the temp access token
        boolean authPass = false;
        IServiceManager manager = getServiceManager();
        try {
            IRemoteDeviceDiscoverer discoverer = IRemoteDeviceDiscoverer.Stub
                    .asInterface(manager.getService(ServiceManager.SERVICE_DESKTOP_DISCOVERER));
            authPass = discoverer.checkTempAccessToken(tempAuthToken);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (!authPass) {
            Log.d(TAG, "temp access token check failed, received access token "
                    + tempAuthToken);
            return false;
        }

        String remoteAddress = data.addr.getHostAddress();
        // find remote device and update it's access token
        Desktop desktop = null;
        try {
            IDesktopManager desktopManager = IDesktopManager.Stub
                    .asInterface(manager.getService(ServiceManager.SERVICE_DESKTOP_MANAGER));
            desktop = desktopManager.findDesktop(remoteAddress, udpPort);
            if (desktop == null) {
                // TODO: create remote device instance according to remote device type
                desktop = new Desktop();
                desktop.setAddress(remoteAddress);
                desktop.setUdpPort(udpPort);
                desktop.setAccessToken(token);
                desktopManager.addDesktop(desktop);

            } else {
                desktop.setAccessToken(token);
                desktop.setAuthToken(null);
                desktopManager.updateDesktop(desktop);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        NotificationHelper.notifyAuthRequest(MyApplication.getInstance(), desktop);
        return true;
    }
}
