package com.weichen2046.filesender2.service

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by chenwei on 6/7/17.
 */
class Phone : RemoteDevice {
    init {
        type = DEVICE_TYPE_PHONE
    }

    constructor()

    constructor(source: Parcel): super(source)

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<Phone> = object : Parcelable.Creator<Phone> {
            override fun createFromParcel(source: Parcel): Phone {
                return Phone(source)
            }

            override fun newArray(size: Int): Array<Phone?> {
                return arrayOfNulls(size)
            }
        }
    }
}