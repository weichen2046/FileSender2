// IDesktopManager.aidl
package com.weichen2046.filesender2.service;

// Declare any non-default types here with import statements
import com.weichen2046.filesender2.service.Desktop;

interface IDesktopManager {
    Desktop findDesktop(String address, int udpPort);
    boolean addDesktop(in Desktop desktop);
    boolean updateDesktop(in Desktop desktop);
}
