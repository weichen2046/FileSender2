// IPCDiscoverer.aidl
package com.weichen2046.filesender2.service;

// Declare any non-default types here with import statements

interface IPCDiscoverer {

    /* Send network broadcast to notify ourself is here.
     *
     * @param times The times of notification.
     * @param pcPort The PC side broadcast listen port.
     */
    void sayHello(int times, int pcPort);
}
