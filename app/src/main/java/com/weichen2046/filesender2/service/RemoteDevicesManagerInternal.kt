package com.weichen2046.filesender2.service

import android.content.Intent
import android.os.RemoteException
import android.util.Log

import com.weichen2046.filesender2.MyApplication

import java.util.ArrayList

/**
 * Created by chenwei on 2017/3/18.
 */

class RemoteDevicesManagerInternal : IRemoteDevicesManager.Stub() {

    private val mDesktops = ArrayList<Desktop>()
    private val mDevices = ArrayList<RemoteDevice>()
    private val mWrappers = ArrayList<RemoteDeviceWrapper<RemoteDevice>>()

    enum class DeviceChangedType {
        ADD,
        DELETE,
        UPDATE
    }

    @Deprecated("Will remove later")
    @Throws(RemoteException::class)
    override fun findDesktop(address: String, udpPort: Int): Desktop? {
        return mDesktops.firstOrNull { it.udpPort == udpPort && it.address == address }
    }

    @Deprecated("Will remove later")
    @Throws(RemoteException::class)
    override fun findDesktopByAuthToken(address: String, authToken: String): Desktop? {
        return mDesktops.firstOrNull { it.address == address && it.authToken == authToken }
    }

    @Deprecated("Will remove later")
    @Throws(RemoteException::class)
    override fun findDesktopByDesktop(desktop: Desktop): Desktop? {
        return mDesktops.firstOrNull { it.equals(desktop) }
    }

    @Deprecated("Will remove later")
    @Throws(RemoteException::class)
    override fun addDesktop(desktop: Desktop): Boolean {
        if (mDesktops.contains(desktop)) {
            return false
        }
        mDesktops.add(desktop)
        notifyDeviceChanged(DeviceChangedType.ADD)
        return true
    }

    @Deprecated("Will remove later")
    @Throws(RemoteException::class)
    override fun deleteDesktop(desktop: Desktop): Boolean {
        val res = mDesktops.remove(desktop)
        if (res) {
            notifyDeviceChanged(DeviceChangedType.DELETE)
        }
        return res
    }

    @Deprecated("Will remove later")
    @Throws(RemoteException::class)
    override fun deleteDesktopByPort(address: String, udpPort: Int): Boolean {
        val desktop = findDesktop(address, udpPort)
        // tcp port is 0 means the desktop is not authenticated
        // NOTE: desktop's access token or auth token can not use to determine authentication state
        if (desktop != null && desktop.tcpPort == 0) {
            return deleteDesktop(desktop)
        }
        return false
    }

    @Deprecated("Will remove later")
    @Throws(RemoteException::class)
    override fun deleteDesktopByAuthToken(address: String, authToken: String): Boolean {
        val desktop = findDesktopByAuthToken(address, authToken)
        if (desktop != null) {
            return deleteDesktop(desktop)
        }
        return false
    }

    @Deprecated("Will remove later")
    @Throws(RemoteException::class)
    override fun updateDesktop(desktop: Desktop): Boolean {
        val desktop1 = findDesktopByDesktop(desktop)
        if (desktop1 != null) {
            desktop1.update(desktop)
            notifyDeviceChanged(DeviceChangedType.UPDATE)
            return true
        }
        return false
    }

    @Deprecated("Will remove later")
    @Throws(RemoteException::class)
    override fun getDesktopCount(): Int {
        return mDesktops.size
    }

    @Deprecated("Will remove later")
    @Throws(RemoteException::class)
    override fun getAllDesktops(): List<*> {
        return mDesktops
    }

    @Deprecated("Will remove later")
    @Throws(RemoteException::class)
    override fun getAllRemoteDevices(): List<*> {
        return mWrappers
    }

    @Throws(RemoteException::class)
    override fun addDevice(wrapper: RemoteDeviceWrapper<RemoteDevice>): Boolean {
        val device = wrapper.innerObj
        if (mDevices.contains(device)) {
            return false
        }
        mDevices.add(device)
        mWrappers.add(wrapper)
        notifyDeviceChanged(DeviceChangedType.ADD)
        return true
    }

    @Throws(RemoteException::class)
    override fun deleteDevice(wrapper: RemoteDeviceWrapper<RemoteDevice>): Boolean {
        val device = wrapper.innerObj
        val res = mDevices.remove(device)
        val res2 = mWrappers.remove(wrapper)
        if (res != res2) {
            Log.w(RemoteDevicesManager.TAG, "is in devices: $res, is in wrappers: $res2")
        }
        if (res) {
            notifyDeviceChanged(DeviceChangedType.DELETE)
        }
        return false
    }

    private fun notifyDeviceChanged(type: DeviceChangedType) {
        val intent = Intent(RemoteDevicesManager.ACTION_DESKTOP_CHANGES)
        intent.putExtra(RemoteDevicesManager.EXTRA_CHANGE_TYPE, type)
        MyApplication.instance!!.sendBroadcast(intent)
    }
}
