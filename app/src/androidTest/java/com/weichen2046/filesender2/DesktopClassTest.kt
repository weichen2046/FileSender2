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
    lateinit var desktop: Desktop;

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

        desktop2.address = "10.101.2.248"
        assertNotEquals(desktop, desktop2)

        desktop.address = "10.101.2.248"
        assertEquals(desktop, desktop2)

        desktop2.udpPort = 6523
        assertNotEquals(desktop, desktop2)

        desktop.udpPort = 6523
        assertEquals(desktop, desktop2)

        // currently equals(...) only compare filed type, udpPort and address, so tcpPort doesn't
        // count
        desktop2.tcpPort = 6222
        assertEquals(desktop, desktop2)
    }

    @Test
    fun test_ParcelableWriteRead() {
        val address = "10.101.2.248"
        val udpPort = 6523
        val tcpPort = 6222
        val nickname = "Test Desktop"
        val authToken = "6146d035076d4c41a35aee9a392e572f"
        val accessToken = "feb10290b53f463d869dd69de5c99a5a"
        desktop.address = address
        desktop.udpPort = udpPort
        desktop.tcpPort = tcpPort
        desktop.nickname = nickname
        desktop.authToken = authToken
        desktop.accessToken = accessToken

        val parcel = Parcel.obtain()

        // test write
        desktop.writeToParcel(parcel, desktop.describeContents())

        // test read
        parcel.setDataPosition(0)
        val readDesktop = Desktop.CREATOR.createFromParcel(parcel)

        assertEquals(address, readDesktop.address)
        assertEquals(udpPort, readDesktop.udpPort)
        assertEquals(tcpPort, readDesktop.tcpPort)
        assertEquals(nickname, readDesktop.nickname)
        assertEquals(authToken, readDesktop.authToken)
        assertEquals(accessToken, readDesktop.accessToken)

        parcel.recycle()
    }
}