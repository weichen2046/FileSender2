package com.weichen2046.filesender2

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.SmallTest
import com.weichen2046.filesender2.service.Desktop
import com.weichen2046.filesender2.service.Phone
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
class PhoneClassTest {
    lateinit var phone: Phone

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
        phone = Phone()
    }

    @Test
    fun test_type() {
        assertEquals("Phone instance type should be RemoteDevice.DEVICE_TYPE_PHONE",
                RemoteDevice.DEVICE_TYPE_PHONE, phone.type)
    }

    @Test
    fun test_equals() {
        val phone2 = Phone()
        assertEquals(phone, phone2)
        assertEquals(true, phone == phone2)
        assertEquals(false, phone === phone2)

        phone2.address = ADDRESS
        assertNotEquals(phone, phone2)

        phone.address = ADDRESS
        assertEquals(phone, phone2)

        phone2.udpPort = UDP_PORT
        assertNotEquals(phone, phone2)

        phone.udpPort = UDP_PORT
        assertEquals(phone, phone2)

        // currently equals(...) only compare filed type, udpPort and address, so tcpPort doesn't
        // count
        phone2.tcpPort = TCP_PORT
        assertEquals(phone, phone2)
    }

    @Test
    fun test_ParcelableWriteRead() {
        phone.address = ADDRESS
        phone.udpPort = UDP_PORT
        phone.tcpPort = TCP_PORT
        phone.nickname = NICKNAME
        phone.authToken = AUTH_TOKEN
        phone.accessToken = ACCESS_TOKEN

        val parcel = Parcel.obtain()

        // test write
        phone.writeToParcel(parcel, phone.describeContents())

        // test read
        parcel.setDataPosition(0)
        val readPhone = Desktop.CREATOR.createFromParcel(parcel)

        with(readPhone) {
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