package com.weichen2046.filesender2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ServiceTestRule
import android.support.test.runner.AndroidJUnit4
import com.weichen2046.filesender2.service.*
import org.hamcrest.Matchers.notNullValue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * Created by chenwei on 2017/6/8.
 */
@RunWith(AndroidJUnit4::class)
class RemoteDevicesManagerInternalTest {
    @Rule @JvmField
    val serviceRule = ServiceTestRule()

    private val devicesManager: IRemoteDevicesManager by lazy {
        getDeviceManager()
    }

    @Test
    fun test_getAllRemoteDevices() {
        val wrappers = devicesManager.allRemoteDevices as ArrayList<RemoteDeviceWrapper<RemoteDevice>>
        assertEquals("Remote devices size should be 0", 0, wrappers.size)

        val desktop = Desktop()
        desktop.udpPort = 6523
        desktop.address = "10.101.2.248"
        val res = devicesManager.addDevice(RemoteDeviceWrapper(desktop))
        assertEquals("Add device failed", true, res)
        assertEquals("Total device count should be 1", 1, devicesManager.allRemoteDevices.size)

        var deviceWrappers = devicesManager.allRemoteDevices as ArrayList<RemoteDeviceWrapper<RemoteDevice>>
        assertEquals("Added device should in returned device list", true,
                deviceWrappers.any { it.innerObj.equals(desktop) })

        val phone = Phone()
        phone.udpPort = 6523
        phone.address = "10.101.2.248"
        assertEquals("Device that not added should not in returned device list", false,
                deviceWrappers.any { it.innerObj.equals(phone) })
    }

    @Test
    fun test_addDevice() {
        val uuid = UUID.randomUUID().toString()
        val desktop = Desktop()
        desktop.udpPort = 6523
        desktop.address = uuid
        val sizeOrigin = devicesManager.allRemoteDevices.size
        var res = devicesManager.addDevice(RemoteDeviceWrapper(desktop))
        assertEquals("Add device failed", true, res)
        assertEquals("Total device count should be ${sizeOrigin + 1}", sizeOrigin + 1,
                devicesManager.allRemoteDevices.size)

        res = devicesManager.addDevice(RemoteDeviceWrapper(desktop))
        assertEquals("Add the same device should be failed", false, res)
        assertEquals("Total device count should be ${sizeOrigin + 1}", sizeOrigin + 1,
                devicesManager.allRemoteDevices.size)

        val desktop2 = Desktop()
        desktop2.udpPort = 6523
        desktop2.address = uuid
        res = devicesManager.addDevice(RemoteDeviceWrapper(desktop2))
        assertEquals("Add device that equals any exist device should be failed", false, res)
        assertEquals("Total device count should be ${sizeOrigin + 1}", sizeOrigin + 1,
                devicesManager.allRemoteDevices.size)

        val phone = Phone()
        phone.udpPort = 6523
        phone.address = uuid
        res = devicesManager.addDevice(RemoteDeviceWrapper(phone))
        assertEquals("Add device with diffrent type should be ok", true, res)
        assertEquals("Total device count should be ${sizeOrigin + 2}", sizeOrigin + 2,
                devicesManager.allRemoteDevices.size)
    }

    private fun getDeviceManager(): IRemoteDevicesManager {
        val managerIntent = Intent(InstrumentationRegistry.getTargetContext(),
                ServiceManagerInternal::class.java)
        val countDown = CountDownLatch(1)
        var binder: IBinder? = null
        serviceRule.bindService(managerIntent, object: ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                binder = service
                countDown.countDown()
            }
        }, Context.BIND_AUTO_CREATE)
        countDown.await()
        assertThat(binder, notNullValue())

        val serviceManager = IServiceManager.Stub.asInterface(binder)
        val devicesManager = IRemoteDevicesManager.Stub
                .asInterface(serviceManager.getService(ServiceManagerInternal.SERVICE_DEVICES_MANAGER))
        assertThat(devicesManager, notNullValue())
        return devicesManager
    }
}