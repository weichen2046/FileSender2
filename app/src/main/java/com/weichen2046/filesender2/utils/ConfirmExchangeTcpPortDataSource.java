package com.weichen2046.filesender2.utils;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.utils.byteconvertor.IntBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.StringBytesConvertor;

/**
 * Created by chenwei on 2017/3/22.
 */

public class ConfirmExchangeTcpPortDataSource extends ByteDataSource {
    private Desktop mDesktop;

    public ConfirmExchangeTcpPortDataSource(Desktop desktop) {
        mDesktop = desktop;
    }
    @Override
    public boolean onInit() {
        super.onInit();
        // 4 bytes -> cmd version
        fillData(new IntBytesConvertor(INetworkDefs.DATA_VERSION));
        // 4 bytes -> cmd
        fillData(new IntBytesConvertor(INetworkDefs.CMD_CONFIRM_EXCHANGE_TCP_PORT));
        // 4 bytes -> access token length
        fillData(new IntBytesConvertor(mDesktop.accessToken.length()));
        // x bytes -> access token
        fillData(new StringBytesConvertor(mDesktop.accessToken));
        // 4 bytes -> tcp port
        fillData(new IntBytesConvertor(INetworkDefs.DEFAULT_DESKTOP_TCP_PORT));
        return true;
    }
}