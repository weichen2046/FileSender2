package com.weichen2046.filesender2

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.SmallTest
import com.weichen2046.filesender2.service.Desktop
import com.weichen2046.filesender2.service.RemoteDevice
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Created by chenwei on 6/9/17.
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
class DesktopClassTest {
    lateinit var desktop: Desktop

    companion object {
        private const val ADDRESS = "10.101.2.248"
        private const val UDP_PORT = 6523
        private const val TCP_PORT = 6222
        private const val NICKNAME = "Test Desktop"
        private const val AUTH_TOKEN = "6146d035076d4c41a35aee9a392e572f"
        private const val ACCESS_TOKEN = "feb10290b53f463d869dd69de5c99a5a"
    }

    @Before
    fun setUp() {
        desktop = Desktop()
    }

    @Test
    fun test_type() {
        assertEquals("Desktop instance type should be RemoteDevice.DEVICE_TYPE_DESKTOP",
                RemoteDevice.DEVICE_TYPE_DESKTOP, desktop.type)
    }

    @Test
    fun test_equals() {
        val desktop2 = Desktop()
        assertEquals(desktop, desktop2)
        assertEquals(true, desktop == desktop2)
        assertEquals(false, desktop === desktop2)

        desktop2.address = ADDRESS
        assertNotEquals(desktop, desktop2)

        desktop.address = ADDRESS
        assertEquals(desktop, desktop2)

        desktop2.udpPort = UDP_PORT
        assertNotEquals(desktop, desktop2)

        desktop.udpPort = UDP_PORT
        assertEquals(desktop, desktop2)

        // currently equals(...) only compare filed type, udpPort and address, so tcpPort doesn't
        // count
        desktop2.tcpPort = TCP_PORT
        assertEquals(desktop, desktop2)
    }

    @Test
    fun test_ParcelableWriteRead() {
        desktop.address = ADDRESS
        desktop.udpPort = UDP_PORT
        desktop.tcpPort = TCP_PORT
        desktop.nickname = NICKNAME
        desktop.authToken = AUTH_TOKEN
        desktop.accessToken = ACCESS_TOKEN

        val parcel = Parcel.obtain()

        // test write
        desktop.writeToParcel(parcel, desktop.describeContents())

        // test read
        parcel.setDataPosition(0)
        val readDesktop = Desktop.CREATOR.createFromParcel(parcel)

        with(readDesktop) {
            assertEquals(ADDRESS, address)
            assertEquals(UDP_PORT, udpPort)
            assertEquals(TCP_PORT, tcpPort)
            assertEquals(NICKNAME, nickname)
            assertEquals(AUTH_TOKEN, authToken)
            assertEquals(ACCESS_TOKEN, accessToken)
        }

        parcel.recycle()
    }
}