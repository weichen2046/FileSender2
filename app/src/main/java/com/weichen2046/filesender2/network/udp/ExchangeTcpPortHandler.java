package com.weichen2046.filesender2.network.udp;

import android.os.RemoteException;

import com.weichen2046.filesender2.MyApplication;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.IDesktopManager;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManager;
import com.weichen2046.filesender2.service.SocketTaskService;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/3/22.
 */

public class ExchangeTcpPortHandler extends UdpCmdHandler {
    public ExchangeTcpPortHandler(int cmd) {
        super(cmd);
    }

    @Override
    public void handle(BroadcastData data) {
        ByteBuffer buffer = ByteBuffer.wrap(data.data);
        // read access token length
        int tokenLength = buffer.getInt();
        // read access token
        byte[] tokenBytes = new byte[tokenLength];
        buffer.get(tokenBytes);
        String authToken = new String(tokenBytes);
        // find desktop by address and access token
        Desktop desktop = findDesktop(data.addr.getHostAddress(), authToken);
        if (desktop != null) {
            int tcpPort = buffer.getInt();
            desktop.tcpPort = tcpPort;
            updateDesktop(desktop);
            SocketTaskService.confirmExchangeTcpPort(MyApplication.getInstance(), desktop);
        }
    }

    private Desktop findDesktop(String address, String authToken) {
        IServiceManager manager = get();
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

    private void updateDesktop(Desktop desktop) {
        IServiceManager manager = get();
        try {
            IDesktopManager desktopManager =IDesktopManager.Stub.asInterface(
                    manager.getService(ServiceManager.SERVICE_DESKTOP_MANAGER));
            desktopManager.updateDesktop(desktop);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
