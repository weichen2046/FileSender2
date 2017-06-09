package com.weichen2046.filesender2

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.SmallTest
import com.weichen2046.filesender2.service.Desktop
import com.weichen2046.filesender2.service.RemoteDeviceWrapper
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Created by chenwei on 6/9/17.
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemoteDeviceWrapperTest {
    @Test
    fun test_ParcelableWriteRead() {
        val parcel: Parcel = Parcel.obtain()

        val desktop: Desktop = Desktop()
        desktop.address = "10.101.2.248"

        val wrapper: RemoteDeviceWrapper<Desktop> = RemoteDeviceWrapper(desktop)

        // test write
        wrapper.writeToParcel(parcel, wrapper.describeContents())

        // test read
        parcel.setDataPosition(0)
        val readWrapper = RemoteDeviceWrapper.CREATOR.createFromParcel(parcel)

        val innerObj = readWrapper.innerObj
        assertEquals(true, innerObj is Desktop)
        assertEquals("10.101.2.248", innerObj.address)

        parcel.recycle()
    }
}