package com.weichen2046.filesender2

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.weichen2046.filesender2.service.ServiceManager
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

/**
 * Created by chenwei on 2017/6/13.
 */
@RunWith(AndroidJUnit4::class)
class ServiceManagerTest {
    @Before
    fun setUp() {
        val latch = CountDownLatch(1)
        val context = InstrumentationRegistry.getTargetContext()
        ServiceManager.init(context, {
            latch.countDown()
        }, {})
        latch.await()
    }

    @Test
    fun test_servicesExist() {
        assertThat(ServiceManager.serviceManager, notNullValue())
        assertThat(ServiceManager.tcpDataMonitor, notNullValue())
        assertThat(ServiceManager.udpDataMonitor, notNullValue())
        assertThat(ServiceManager.remoteDevicesManager, notNullValue())
        assertThat(ServiceManager.deviceDiscoverer, notNullValue())
    }
}