package com.weichen2046.filesender2.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.util.SparseArray

class ServiceManagerInternal : Service() {

    private val mSubServices = SparseArray<IBinder>()

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    private val mBinder = object : IServiceManager.Stub() {
        @Synchronized @Throws(RemoteException::class)
        override fun getService(serviceId: Int): IBinder {
            var binder: IBinder? = mSubServices.get(serviceId)
            if (binder != null) {
                return binder
            }

            when(serviceId) {
                SERVICE_DEVICE_DISCOVERER -> {
                    binder = RemoteDeviceDiscoverer()
                    mSubServices.put(SERVICE_DEVICE_DISCOVERER, binder)
                }
                SERVICE_UDP_DATA_MONITOR -> {
                    val udpDataMonitor = UdpDataMonitor()
                    udpDataMonitor.attach(IServiceManager.Stub.asInterface(this))
                    binder = udpDataMonitor
                    mSubServices.put(SERVICE_UDP_DATA_MONITOR, binder)
                }
                SERVICE_TCP_DATA_MONITOR -> {
                    val tcpDataMonitor = TcpDataMonitor()
                    tcpDataMonitor.attach(IServiceManager.Stub.asInterface(this))
                    binder = tcpDataMonitor
                    mSubServices.put(SERVICE_TCP_DATA_MONITOR, binder)
                }
                SERVICE_DEVICES_MANAGER -> {
                    binder = RemoteDevicesManagerInternal()
                    mSubServices.put(SERVICE_DEVICES_MANAGER, binder)
                }
                else -> throw IllegalArgumentException("invalid service id $serviceId")
            }
            return binder
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called.")
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind called.")
        return super.onUnbind(intent)
    }

    companion object {

        private val TAG = "ServiceManagerInternal"

        const val SERVICE_DEVICE_DISCOVERER = 1
        const val SERVICE_UDP_DATA_MONITOR = 2
        const val SERVICE_TCP_DATA_MONITOR = 3
        const val SERVICE_DEVICES_MANAGER = 4
    }
}
