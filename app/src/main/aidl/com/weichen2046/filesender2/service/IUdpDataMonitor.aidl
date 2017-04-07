// IUdpDataMonitor.aidl
package com.weichen2046.filesender2.service;

// Declare any non-default types here with import statements

interface IUdpDataMonitor {
    /**
     * Start broadcast monitor.
     */
    boolean start();

    /**
     * Stop broadcast monitor.
     */
    boolean stop();
}
