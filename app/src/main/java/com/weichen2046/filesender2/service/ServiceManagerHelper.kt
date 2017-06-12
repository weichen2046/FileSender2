package com.weichen2046.filesender2.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

/**
 * Created by chenwei on 2017/6/12.
 */
object ServiceManagerHelper {
    var serviceManager: IServiceManager? = null
        private set

    var remoteDevicesManager: IRemoteDevicesManager? = null
        private set

    fun init(context: Context, connectedCallback: () -> Unit, disconnectedCallback: () -> Unit) {
        val serviceIntent = Intent(context, ServiceManager::class.java)
        context.bindService(serviceIntent, object: ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                disconnectedCallback()
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                serviceManager = IServiceManager.Stub.asInterface(service)
                val binder = serviceManager?.getService(ServiceManager.SERVICE_DEVICES_MANAGER)
                remoteDevicesManager = IRemoteDevicesManager.Stub.asInterface(binder)
                connectedCallback()
            }
        }, Context.BIND_AUTO_CREATE)
    }
}