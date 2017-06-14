package com.weichen2046.filesender2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ServiceTestRule
import android.support.test.runner.AndroidJUnit4

import com.weichen2046.filesender2.service.IServiceManager
import com.weichen2046.filesender2.service.ServiceManagerInternal
import org.hamcrest.Matchers.notNullValue

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.concurrent.CountDownLatch

import java.util.concurrent.TimeoutException

/**
 * Created by chenwei on 2017/6/8.
 */

@RunWith(AndroidJUnit4::class)
class ServiceManagerInternalTest {

    @Rule @JvmField
    val serviceRule = ServiceTestRule()

    private var serviceIntent: Intent = Intent(InstrumentationRegistry.getTargetContext(),
                ServiceManagerInternal::class.java)

    @Test
    fun testWithBoundService() {
        val countDown = CountDownLatch(1)
        var binder: IBinder? = null
        try {
            serviceRule.bindService(serviceIntent, object: ServiceConnection{
                override fun onServiceDisconnected(name: ComponentName?) {
                }
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    binder = service
                    countDown.countDown()
                }
            }, Context.BIND_AUTO_CREATE)
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }
        countDown.await()
        assertThat(binder, notNullValue())
    }

    @Test
    fun test_getService() {
        val countDown = CountDownLatch(1)
        var binder: IBinder? = null
        try {
            serviceRule.bindService(serviceIntent, object: ServiceConnection{
                override fun onServiceDisconnected(name: ComponentName?) {
                }
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    binder = service
                    countDown.countDown()
                }
            }, Context.BIND_AUTO_CREATE)
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }
        countDown.await()
        val serviceManager = IServiceManager.Stub.asInterface(binder)
        assertNotNull("Can not bind ServiceManagerInternal", serviceManager)

        try {
            binder = serviceManager.getService(ServiceManagerInternal.SERVICE_DEVICE_DISCOVERER)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        assertNotNull("Can not get service by SERVICE_DEVICE_DISCOVERER", binder)

        try {
            binder = serviceManager.getService(ServiceManagerInternal.SERVICE_UDP_DATA_MONITOR)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        assertNotNull("Can not get service by SERVICE_UDP_DATA_MONITOR", binder)

        try {
            binder = serviceManager.getService(ServiceManagerInternal.SERVICE_TCP_DATA_MONITOR)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        assertNotNull("Can not get service by SERVICE_TCP_DATA_MONITOR", binder)

        try {
            binder = serviceManager.getService(ServiceManagerInternal.SERVICE_DEVICES_MANAGER)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        assertNotNull("Can not get service by SERVICE_DEVICES_MANAGER", binder)
    }
}
