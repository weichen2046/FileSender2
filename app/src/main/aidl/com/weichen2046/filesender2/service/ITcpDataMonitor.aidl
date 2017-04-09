// ITcpDataMonitor.aidl
package com.weichen2046.filesender2.service;

// Declare any non-default types here with import statements

interface ITcpDataMonitor {
    /**
     * Start tcp server.
     */
    boolean start();

    /**
     * Stop tcp server.
     */
    boolean stop();
}
