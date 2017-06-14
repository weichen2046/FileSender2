package com.weichen2046.filesender2

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.weichen2046.filesender2.service.Desktop
import com.weichen2046.filesender2.service.RemoteDevice
import com.weichen2046.filesender2.service.RemoteDevicesManager
import com.weichen2046.filesender2.service.ServiceManager
import org.hamcrest.Matchers.hasItemInArray
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * Created by chenwei on 6/14/17.
 */
@RunWith(AndroidJUnit4::class)
class RemoteDevicesManagerTest {
    private var deviceManager: RemoteDevicesManager? = null

    @Before
    fun setUp() {
        val latch = CountDownLatch(1)
        val context = InstrumentationRegistry.getTargetContext()
        ServiceManager.init(context, {
            latch.countDown()
        }, {})
        latch.await()
        deviceManager = ServiceManager.remoteDevicesManager
    }

    @Test
    fun test_getAllRemoteDevices() {
        var devices = deviceManager?.allRemoteDevices
        assertThat(devices, notNullValue())

        var sizeOrigin = devices!!.size
        val desktop = Desktop()
        desktop.address = UUID.randomUUID().toString()

        deviceManager?.addDevice(desktop)
        devices = deviceManager?.allRemoteDevices!!
        assertThat("device collection should has 1 element", devices.size, `is`(sizeOrigin + 1))
        assertThat("device collection should contains added desktop $desktop",
                devices.toTypedArray(), hasItemInArray(desktop as RemoteDevice))
    }

    @Test
    fun test_addDevice() {
        val desktop = Desktop()
        desktop.address = UUID.randomUUID().toString()

        val size = deviceManager?.allRemoteDevices!!.size
        deviceManager?.addDevice(desktop)
        val size2 = deviceManager?.allRemoteDevices!!.size
        assertThat("after add, size of remote devices not change", size2, `is`(size + 1))
    }

    @Test
    fun test_deleteDevice() {
        val desktop = Desktop()
        desktop.address = UUID.randomUUID().toString()

        val size = deviceManager?.allRemoteDevices!!.size
        deviceManager?.addDevice(desktop)
        deviceManager?.deleteDevice(desktop)
        val size2 = deviceManager?.allRemoteDevices!!.size
        assertThat("after delete, size of remote devices not change", size2, `is`(size))
    }
}
