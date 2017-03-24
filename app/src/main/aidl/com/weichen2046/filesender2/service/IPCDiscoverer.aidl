// IPCDiscoverer.aidl
package com.weichen2046.filesender2.service;

// Declare any non-default types here with import statements

interface IPCDiscoverer {

    /**
     * Send network broadcast to notify ourself is here.
     *
     * @param address The destination desktop address, null means broadcast address.
     * @param port The PC side broadcast listen port.
     */
    void sayHello(String address, int port);

    boolean checkTempAccessToken(String token);
}
