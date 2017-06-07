package com.weichen2046.filesender2.utils.udpdatasource

import com.weichen2046.filesender2.network.INetworkDefs
import com.weichen2046.filesender2.utils.ByteDataSource
import com.weichen2046.filesender2.utils.byteconvertor.IntBytesConvertor
import com.weichen2046.filesender2.utils.byteconvertor.StringBytesConvertor

/**
 * Created by chenwei on 2017/6/7.
 */
class SayHelloDataSource(private val tmpAuthToken: String) : ByteDataSource() {
    override fun onInit(): Boolean {
        super.onInit()
        // 4 bytes -> cmd version
        fillData(IntBytesConvertor(INetworkDefs.DATA_VERSION))
        // 4 bytes -> cmd
        fillData(IntBytesConvertor(INetworkDefs.CMD_T_PHONE_ONLINE))
        // 4 bytes -> temp auth token length
        fillData(IntBytesConvertor(tmpAuthToken.length))
        // x bytes -> temp auth token
        fillData(StringBytesConvertor(tmpAuthToken))
        // 4 bytes -> phone udp listen port
        fillData(IntBytesConvertor(INetworkDefs.MOBILE_UDP_LISTEN_PORT))
        return true
    }
}