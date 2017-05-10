package com.weichen2046.filesender2.network.udp;

import android.os.RemoteException;

import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.IDesktopManager;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManager;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/4/7.
 */

public class UdpAuthCmdHandler extends UdpCmdHandler {
    protected ByteBuffer mBuffer;
    protected Desktop mDesktop;

    public UdpAuthCmdHandler(int cmd) {
        super(cmd);
    }

    @Override
    public boolean handle(BroadcastData data) {
        mBuffer = ByteBuffer.wrap(data.data);
        // read access token length
        int tokenLength = mBuffer.getInt();
        // read access token
        byte[] tokenBytes = new byte[tokenLength];
        mBuffer.get(tokenBytes);
        String authToken = new String(tokenBytes);
        // find desktop by address and access token
        mDesktop = findDesktop(data.addr.getHostAddress(), authToken);
        return mDesktop != null;
    }

    private Desktop findDesktop(String address, String authToken) {
        IServiceManager manager = getServiceManager();
        Desktop desktop = null;
        try {
            IDesktopManager desktopManager =IDesktopManager.Stub.asInterface(
                    manager.getService(ServiceManager.SERVICE_DESKTOP_MANAGER));
            desktop = desktopManager.findDesktopByAuthToken(address, authToken);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return desktop;
    }
}
