package com.weichen2046.filesender2

import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ServiceTestRule
import android.support.test.runner.AndroidJUnit4

import com.weichen2046.filesender2.service.IServiceManager
import com.weichen2046.filesender2.service.ServiceManager

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

import java.util.concurrent.TimeoutException

/**
 * Created by chenwei on 2017/6/8.
 */

@RunWith(AndroidJUnit4::class)
class ServiceManagerTest {

    @Rule @JvmField
    val serviceRule = ServiceTestRule()

    private var serviceIntent: Intent = Intent(InstrumentationRegistry.getTargetContext(),
                ServiceManager::class.java)

    @Test
    fun testWithBoundService() {
        var binder: IBinder? = null
        try {
            binder = serviceRule.bindService(serviceIntent)
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }
        assertNotNull("Can not bind ServiceManager", binder)
    }

    @Test
    fun test_getService() {
        var binder: IBinder? = null
        try {
            binder = serviceRule.bindService(serviceIntent)
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }
        val serviceManager = IServiceManager.Stub.asInterface(binder)
        assertNotNull("Can not bind ServiceManager", serviceManager)

        try {
            binder = serviceManager.getService(ServiceManager.SERVICE_DEVICE_DISCOVERER)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        assertNotNull("Can not get service by SERVICE_DEVICE_DISCOVERER", binder)

        try {
            binder = serviceManager.getService(ServiceManager.SERVICE_UDP_DATA_MONITOR)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        assertNotNull("Can not get service by SERVICE_UDP_DATA_MONITOR", binder)

        try {
            binder = serviceManager.getService(ServiceManager.SERVICE_TCP_DATA_MONITOR)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        assertNotNull("Can not get service by SERVICE_TCP_DATA_MONITOR", binder)

        try {
            binder = serviceManager.getService(ServiceManager.SERVICE_DEVICES_MANAGER)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        assertNotNull("Can not get service by SERVICE_DEVICES_MANAGER", binder)
    }
}
