package com.weichen2046.filesender2.service

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by chenwei on 2017/5/20.
 */
open class RemoteDevice: Parcelable {

    var nickname: String? = null
    var address: String? = null
    var udpPort: Int = 0
    var tcpPort: Int = 0
    // token use to access remote device, we send this to remove device for authenticating
    var accessToken: String? = null
    // token use to authenticate remote device, remote device will send this back for authenticating
    var authToken: String? = null
    var type: Int = DEVICE_TYPE_UNKNOWN

    constructor()

    constructor(source: Parcel) {
        nickname = source.readString()
        address = source.readString()
        udpPort = source.readInt()
        tcpPort = source.readInt()
        accessToken = source.readString()
        authToken = source.readString()
        type = source.readInt()
    }

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

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        val right = other as RemoteDevice
        return type == right.type
                && udpPort == right.udpPort
                && address == right.address
    }

    override fun toString(): String {
        return "RemoteDevice [ nickname: $nickname, address: $address, udpPort: $udpPort," +
                " tcpPort: $tcpPort, accessToken: $accessToken, authToken: $authToken," +
                " type: $type]"
    }

    companion object {
        @JvmField val DEVICE_TYPE_UNKNOWN = -1
        @JvmField val DEVICE_TYPE_DESKTOP = 1
        @JvmField val DEVICE_TYPE_PHONE = 2
        private val DEVICE_TYPE_MIN = DEVICE_TYPE_DESKTOP
        private val DEVICE_TYPE_MAX = DEVICE_TYPE_PHONE

        @JvmStatic fun isValidDeviceType(type: Int) = type in DEVICE_TYPE_MIN..DEVICE_TYPE_MAX
    }

    open fun update(device: RemoteDevice) {
        nickname = device.nickname
        address = device.address
        udpPort = device.udpPort
        tcpPort = device.tcpPort
        accessToken = device.accessToken
        authToken = device.authToken
        type = device.type
    }
}
