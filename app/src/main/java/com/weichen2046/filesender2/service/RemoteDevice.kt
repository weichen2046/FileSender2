package com.weichen2046.filesender2.service

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by chenwei on 2017/5/20.
 */
data class RemoteDevice(
        var nickname: String,
        var address: String,
        var udpPort: Int = 0,
        var tcpPort: Int = 0,
        var accessToken: String,
        var authToken: String,
        var type: Int
) : Parcelable {

    // token use to access remote device, we send this to remove device for authenticating
    // token use to authenticate remote device, remote device will send this back for authenticating

    constructor(source: Parcel): this(
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readInt()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(nickname)
        dest?.writeString(address)
        dest?.writeInt(udpPort)
        dest?.writeInt(tcpPort)
        dest?.writeString(accessToken)
        dest?.writeString(authToken)
        dest?.writeInt(type)
    }

    override fun equals(o: Any?): Boolean {
        if (o == null) {
            return false
        }
        val right = o as Desktop?
        return udpPort == right!!.udpPort && address == right.address
    }

    override fun toString(): String {
        return "RemoteDevice [ nickname: $nickname, address: $address, udpPort: $udpPort," +
                " tcpPort: $tcpPort, accessToken: $accessToken, authToken: $authToken," +
                " type: $type]"
    }

    companion object {

        val CREATOR: Parcelable.Creator<RemoteDevice> = object : Parcelable.Creator<RemoteDevice> {
            override fun createFromParcel(source: Parcel): RemoteDevice {
                return RemoteDevice(source)
            }

            override fun newArray(size: Int): Array<RemoteDevice?> {
                return arrayOfNulls(size)
            }
        }
    }

    fun update(device: RemoteDevice) {
        nickname = device.nickname
        address = device.address
        udpPort = device.udpPort
        tcpPort = device.tcpPort
        accessToken = device.accessToken
        authToken = device.authToken
        type = device.type;
    }
}
