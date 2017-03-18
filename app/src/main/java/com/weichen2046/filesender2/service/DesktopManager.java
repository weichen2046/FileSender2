package com.weichen2046.filesender2.service;

import android.os.RemoteException;

import java.util.ArrayList;

/**
 * Created by chenwei on 2017/3/18.
 */

public class DesktopManager extends IDesktopManager.Stub {
    private ArrayList<Desktop> mDesktops = new ArrayList<>();

    @Override
    public Desktop findDesktop(String address, int udpPort) throws RemoteException {
        for (Desktop desktop : mDesktops) {
            if (desktop.udpPort == udpPort && desktop.address.equals(address)) {
                return desktop;
            }
        }
        return null;
    }

    @Override
    public boolean addDesktop(Desktop desktop) throws RemoteException {
        return true;
    }

    @Override
    public boolean updateDesktop(Desktop desktop) throws RemoteException {
        return true;
    }
}
