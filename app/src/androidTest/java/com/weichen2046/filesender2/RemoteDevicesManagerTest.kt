package com.weichen2046.filesender2

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ServiceTestRule
import android.support.test.runner.AndroidJUnit4
import com.weichen2046.filesender2.service.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Created by chenwei on 2017/6/8.
 */
@RunWith(AndroidJUnit4::class)
class RemoteDevicesManagerTest {
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
        val desktop = Desktop()
        desktop.udpPort = 6523
        desktop.address = "10.101.2.248"
        var res = devicesManager.addDevice(RemoteDeviceWrapper(desktop))
        assertEquals("Add device failed", true, res)
        assertEquals("Total device count should be 1", 1, devicesManager.allRemoteDevices.size)

        res = devicesManager.addDevice(RemoteDeviceWrapper(desktop))
        assertEquals("Add the same device should be failed", false, res)
        assertEquals("Total device count should be 1", 1, devicesManager.allRemoteDevices.size)

        val desktop2 = Desktop()
        desktop2.udpPort = 6523
        desktop2.address = "10.101.2.248"
        res = devicesManager.addDevice(RemoteDeviceWrapper(desktop2))
        assertEquals("Add device that equals any exist device should be failed", false, res)
        assertEquals("Total device count should be 1", 1, devicesManager.allRemoteDevices.size)

        val phone = Phone()
        phone.udpPort = 6523
        phone.address = "10.101.2.248"
        res = devicesManager.addDevice(RemoteDeviceWrapper(phone))
        assertEquals("Add device with diffrent type should be ok", true, res)
        assertEquals("Total device count should be 2", 2, devicesManager.allRemoteDevices.size)
    }

    private fun getDeviceManager(): IRemoteDevicesManager {
        val managerIntent = Intent(InstrumentationRegistry.getTargetContext(),
                ServiceManager::class.java)
        val binder = serviceRule.bindService(managerIntent)
        // FIXME: very curious, all test methods will fail because of null binder here except the
        // first test method, but if we run test method individually all methods succeeded.
        assertNotNull("Can not bind service ServiceManager, binder: $binder", binder)

        val serviceManager = IServiceManager.Stub.asInterface(binder)
        val devicesManager = IRemoteDevicesManager.Stub
                .asInterface(serviceManager.getService(ServiceManager.SERVICE_DEVICES_MANAGER))
        assertNotNull("Can not get service RemoteDevicesManager, devicesManager: $devicesManager",
                devicesManager)
        return devicesManager
    }
}