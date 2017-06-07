package com.weichen2046.filesender2.service

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by chenwei on 2017/6/7.
 */
class RemoteDeviceWrapper<T : RemoteDevice> : Parcelable {
    val innerObj: T

    constructor(inner: T) {
        innerObj = inner
    }

    private constructor(source: Parcel) {
        val className = source.readString()
        val cl = Class.forName(className).classLoader
        innerObj = source.readParcelable(cl)
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(innerObj.javaClass.name)
        dest?.writeParcelable(innerObj, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        val right = other as RemoteDeviceWrapper<T>
        return innerObj.equals(right.innerObj)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<RemoteDeviceWrapper<RemoteDevice>>
                = object : Parcelable.Creator<RemoteDeviceWrapper<RemoteDevice>> {
            override fun newArray(size: Int): Array<RemoteDeviceWrapper<RemoteDevice>?> {
                return arrayOfNulls(size)
            }

            override fun createFromParcel(source: Parcel): RemoteDeviceWrapper<RemoteDevice> {
                return RemoteDeviceWrapper(source)
            }

        }
    }
}