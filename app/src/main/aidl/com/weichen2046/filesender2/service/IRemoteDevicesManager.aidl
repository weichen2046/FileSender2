// IDesktopManager.aidl
package com.weichen2046.filesender2.service;

// Declare any non-default types here with import statements
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.RemoteDeviceWrapper;

interface IRemoteDevicesManager {
    Desktop findDesktop(String address, int udpPort);
    Desktop findDesktopByAuthToken(String address, String authToken);
    Desktop findDesktopByDesktop(in Desktop desktop);
    boolean addDesktop(in Desktop desktop);
    boolean deleteDesktop(in Desktop desktop);
    boolean deleteDesktopByPort(String address, int udpPort);
    boolean deleteDesktopByAuthToken(String address, String authToken);
    boolean updateDesktop(in Desktop desktop);
    int getDesktopCount();
    List getAllDesktops();

    List getAllRemoteDevices();
    boolean addDevice(in RemoteDeviceWrapper wrapper);
    boolean deleteDevice(in RemoteDeviceWrapper wrapper);
}
