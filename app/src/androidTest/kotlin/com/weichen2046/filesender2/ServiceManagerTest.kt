package com.weichen2046.filesender2

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.weichen2046.filesender2.service.Desktop
import com.weichen2046.filesender2.service.RemoteDevice
import com.weichen2046.filesender2.service.RemoteDeviceWrapper
import com.weichen2046.filesender2.service.ServiceManager
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

/**
 * Created by chenwei on 2017/6/13.
 */
@RunWith(AndroidJUnit4::class)
class ServiceManagerTest {
    @Test
    fun test_getAllRemoteDevices() {
        val latch = CountDownLatch(1)
        val context = InstrumentationRegistry.getTargetContext()
        ServiceManager.init(context, {
            latch.countDown()
        }, {})
        latch.await()
        var devices = ServiceManager.getAllRemoteDevices()
        assertThat("device collection should be empty", 0, `is`(devices.size))

        val desktop = Desktop()
        desktop.address = "10.101.2.248"

        ServiceManager.remoteDevicesManager?.addDevice(RemoteDeviceWrapper<RemoteDevice>(desktop))
        devices = ServiceManager.getAllRemoteDevices()
        assertThat("device collection should has 1 element", 1, `is`(devices.size))
        assertThat("device collection should contains added desktop $desktop",
                devices.toTypedArray(), arrayContaining(desktop as RemoteDevice))
    }
}