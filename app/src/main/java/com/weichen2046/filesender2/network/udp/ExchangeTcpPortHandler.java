package com.weichen2046.filesender2.network.udp;

import android.os.RemoteException;

import com.weichen2046.filesender2.MyApplication;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.IDesktopManager;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManager;
import com.weichen2046.filesender2.service.SocketTaskService;

/**
 * Created by chenwei on 2017/3/22.
 */

public class ExchangeTcpPortHandler extends UdpAuthCmdHandler {
    public ExchangeTcpPortHandler(int cmd) {
        super(cmd);
    }

    @Override
    public boolean handle(BroadcastData data) {
        if (!super.handle(data)) {
            return false;
        }
        int tcpPort = mBuffer.getInt();
        mDesktop.setTcpPort(tcpPort);
        updateDesktop(mDesktop);
        SocketTaskService.confirmExchangeTcpPort(MyApplication.getInstance(), mDesktop);
        return true;
    }

    private void updateDesktop(Desktop desktop) {
        IServiceManager manager = getServiceManager();
        try {
            IDesktopManager desktopManager =IDesktopManager.Stub.asInterface(
                    manager.getService(ServiceManager.SERVICE_DESKTOP_MANAGER));
            desktopManager.updateDesktop(desktop);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
