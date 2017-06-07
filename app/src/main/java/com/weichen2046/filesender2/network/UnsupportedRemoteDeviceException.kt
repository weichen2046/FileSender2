package com.weichen2046.filesender2.network

/**
 * Created by chenwei on 2017/6/7.
 */
class UnsupportedRemoteDeviceException(deviceType: Int)
    : RuntimeException("Unknown device type: $deviceType")
