package com.weichen2046.filesender2.service;

import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

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
    public Desktop findDesktopByAuthToken(String address, String authToken) throws RemoteException {
        for (Desktop desktop : mDesktops) {
            if (desktop.address.equals(address)) {
                if (desktop.authToken.equals(authToken)) {
                    return desktop;
                }
            }
        }
        return null;
    }

    @Override
    public Desktop findDesktopByDesktop(Desktop desktop) throws RemoteException {
        int index = mDesktops.indexOf(desktop);
        if (index != -1) {
            return mDesktops.get(index);
        }
        return null;
    }

    @Override
    public boolean addDesktop(Desktop desktop) throws RemoteException {
        if (mDesktops.contains(desktop)) {
            return false;
        }
        mDesktops.add(desktop);
        return true;
    }

    @Override
    public boolean deleteDesktop(Desktop desktop) throws RemoteException {
        return mDesktops.remove(desktop);
    }

    @Override
    public boolean deleteDesktopByPort(String address, int udpPort) throws RemoteException {
        Desktop desktop = findDesktop(address, udpPort);
        // tcp port is 0 means the desktop is not authenticated
        // NOTE: desktop's access token or auth token can not use to determine authentication state
        if (desktop != null && desktop.tcpPort == 0) {
            return deleteDesktop(desktop);
        }
        return false;
    }

    @Override
    public boolean deleteDesktopByAuthToken(String address, String authToken) throws RemoteException {
        Desktop desktop = findDesktopByAuthToken(address, authToken);
        if (desktop != null) {
            return deleteDesktop(desktop);
        }
        return false;
    }

    @Override
    public boolean updateDesktop(Desktop desktop) throws RemoteException {
        int index = mDesktops.indexOf(desktop);
        if (index != -1) {
            Desktop desktop1 = mDesktops.get(index);
            desktop1.update(desktop);
            return true;
        }
        return false;
    }

    @Override
    public int getDesktopCount() throws RemoteException {
        return mDesktops.size();
    }

    @Override
    public List getAllDesktops() throws RemoteException {
        return mDesktops;
    }
}
