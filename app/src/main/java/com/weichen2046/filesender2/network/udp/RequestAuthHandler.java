package com.weichen2046.filesender2.network.udp;

import android.os.RemoteException;
import android.util.Log;

import com.weichen2046.filesender2.MyApplication;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.IDesktopDiscoverer;
import com.weichen2046.filesender2.service.IDesktopManager;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManager;
import com.weichen2046.filesender2.utils.NotificationHelper;

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
        // read temp access token length
        int tokenLength = buffer.getInt();
        byte[] tempTokenBytes = new byte[tokenLength];
        buffer.get(tempTokenBytes);
        String tempAccessToken = new String(tempTokenBytes);
        // read auth token length
        tokenLength = buffer.getInt();
        // read auth token
        byte[] tokenBytes = new byte[tokenLength];
        buffer.get(tokenBytes);
        String token = new String(tokenBytes);
        int udpPort = buffer.getInt();

        Log.d(TAG, "hand request auth, temp access token: " + tempAccessToken + ", token: " + token + ", udp port: " + udpPort);

        // authenticate use the temp access token
        boolean authPass = false;
        IServiceManager manager = get();
        try {
            IDesktopDiscoverer discoverer = IDesktopDiscoverer.Stub.asInterface(manager.getService(ServiceManager.SERVICE_DESKTOP_DISCOVERER));
            authPass = discoverer.checkTempAccessToken(tempAccessToken);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (!authPass) {
            Log.d(TAG, "temp access token check failed, received access token " + tempAccessToken);
            return false;
        }

        String desktopAddress = data.addr.getHostAddress();
        // find the desktop and update it access token
        Desktop desktop = null;
        try {
            IDesktopManager desktopManager = IDesktopManager.Stub.asInterface(manager.getService(ServiceManager.SERVICE_DESKTOP_MANAGER));
            desktop = desktopManager.findDesktop(desktopAddress, udpPort);
            if (desktop == null) {
                desktop = new Desktop();
                desktop.address = desktopAddress;
                desktop.udpPort = udpPort;
                desktop.accessToken = token;
                desktopManager.addDesktop(desktop);

            } else {
                desktop.accessToken = token;
                desktop.authToken = null;
                desktopManager.updateDesktop(desktop);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        NotificationHelper.makeAuthRequestNotification(MyApplication.getInstance(), desktop);
        return true;
    }
}
