// IServiceManager.aidl
package com.weichen2046.filesender2.service;

// Declare any non-default types here with import statements

interface IServiceManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    IBinder getService(int serviceId);
}
