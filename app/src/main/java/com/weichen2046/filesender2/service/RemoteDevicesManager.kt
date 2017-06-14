package com.weichen2046.filesender2.service

/**
 * Created by chenwei on 6/14/17.
 */
class RemoteDevicesManager(private val inner: IRemoteDevicesManager)
    : IRemoteDevicesManager by inner {

    override fun getAllRemoteDevices(): ArrayList<RemoteDevice> {
        val list: ArrayList<RemoteDevice> = arrayListOf()
        list.apply {
            inner.allRemoteDevices.map {
                val wrapper = it as RemoteDeviceWrapper<RemoteDevice>
                add(wrapper.innerObj)
            }
        }
        return list
    }

    fun addDevice(device: RemoteDevice): Boolean {
        return inner.addDevice(RemoteDeviceWrapper(device))
    }

    fun deleteDevice(device: RemoteDevice): Boolean {
        return inner.deleteDevice(RemoteDeviceWrapper(device))
    }

    companion object {
        const val TAG = "RemoteDevicesManager"

        const val ACTION_DESKTOP_CHANGES = "action.filesender2.DESKTOP_CHANGES"
        const val EXTRA_CHANGE_TYPE = "extra_change_type"
    }
}