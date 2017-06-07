package com.weichen2046.filesender2.service

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by chenwei on 6/7/17.
 */
class Desktop : RemoteDevice {
    init {
        type = DEVICE_TYPE_DESKTOP
    }

    constructor()

    constructor(source: Parcel): super(source)

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<Desktop> = object : Parcelable.Creator<Desktop> {
            override fun createFromParcel(source: Parcel): Desktop {
                return Desktop(source)
            }

            override fun newArray(size: Int): Array<Desktop?> {
                return arrayOfNulls(size)
            }
        }
    }
}