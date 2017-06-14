package com.weichen2046.filesender2.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

/**
 * Created by chenwei on 2017/6/12.
 */
object ServiceManager {
    var serviceManager: IServiceManager? = null
        private set

    var remoteDevicesManager: RemoteDevicesManager? = null
        private set

    var udpDataMonitor: IUdpDataMonitor? = null
        private set

    var tcpDataMonitor: ITcpDataMonitor? = null
        private set

    var deviceDiscoverer: IRemoteDeviceDiscoverer? = null
        private set

    private var connected: Boolean = false

    fun init(context: Context, connectedCallback: () -> Unit, disconnectedCallback: () -> Unit) {
        if (connected) {
            connectedCallback()
            return
        }
        val serviceIntent = Intent(context, ServiceManagerInternal::class.java)
        context.bindService(serviceIntent, object: ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                connected = false
                serviceManager = null
                remoteDevicesManager = null
                udpDataMonitor = null
                tcpDataMonitor = null
                deviceDiscoverer = null
                disconnectedCallback()
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                connected = true
                serviceManager = IServiceManager.Stub.asInterface(service)
                var binder = serviceManager?.getService(ServiceManagerInternal.SERVICE_DEVICES_MANAGER)
                remoteDevicesManager = RemoteDevicesManager(IRemoteDevicesManager.Stub.asInterface(binder))
                binder = serviceManager?.getService((ServiceManagerInternal.SERVICE_UDP_DATA_MONITOR))
                udpDataMonitor = IUdpDataMonitor.Stub.asInterface(binder)
                binder = serviceManager?.getService((ServiceManagerInternal.SERVICE_TCP_DATA_MONITOR))
                tcpDataMonitor = ITcpDataMonitor.Stub.asInterface(binder)
                binder = serviceManager?.getService((ServiceManagerInternal.SERVICE_DEVICE_DISCOVERER))
                deviceDiscoverer = IRemoteDeviceDiscoverer.Stub.asInterface(binder)
                connectedCallback()
            }
        }, Context.BIND_AUTO_CREATE)
    }
}