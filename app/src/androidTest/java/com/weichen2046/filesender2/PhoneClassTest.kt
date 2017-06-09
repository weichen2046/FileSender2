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

        phone2.address = "10.101.2.248"
        assertNotEquals(phone, phone2)

        phone.address = "10.101.2.248"
        assertEquals(phone, phone2)

        phone2.udpPort = 6523
        assertNotEquals(phone, phone2)

        phone.udpPort = 6523
        assertEquals(phone, phone2)

        // currently equals(...) only compare filed type, udpPort and address, so tcpPort doesn't
        // count
        phone2.tcpPort = 6222
        assertEquals(phone, phone2)
    }

    @Test
    fun test_ParcelableWriteRead() {
        val address = "10.101.2.248"
        val udpPort = 6523
        val tcpPort = 6222
        val nickname = "Test Desktop"
        val authToken = "6146d035076d4c41a35aee9a392e572f"
        val accessToken = "feb10290b53f463d869dd69de5c99a5a"
        phone.address = address
        phone.udpPort = udpPort
        phone.tcpPort = tcpPort
        phone.nickname = nickname
        phone.authToken = authToken
        phone.accessToken = accessToken

        val parcel = Parcel.obtain()

        // test write
        phone.writeToParcel(parcel, phone.describeContents())

        // test read
        parcel.setDataPosition(0)
        val readPhone = Desktop.CREATOR.createFromParcel(parcel)

        assertEquals(address, readPhone.address)
        assertEquals(udpPort, readPhone.udpPort)
        assertEquals(tcpPort, readPhone.tcpPort)
        assertEquals(nickname, readPhone.nickname)
        assertEquals(authToken, readPhone.authToken)
        assertEquals(accessToken, readPhone.accessToken)

        parcel.recycle()
    }
}