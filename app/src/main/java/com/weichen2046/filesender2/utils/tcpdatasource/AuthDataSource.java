package com.weichen2046.filesender2.utils.tcpdatasource;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.utils.ByteDataSource;
import com.weichen2046.filesender2.utils.byteconvertor.IntBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.StringBytesConvertor;

/**
 * Created by chenwei on 2017/5/11.
 */

public class AuthDataSource extends ByteDataSource {
    private int mCmd;
    private Desktop mDevice;

    public AuthDataSource(Desktop device, int cmd) {
        mCmd = cmd;
        mDevice = device;
    }

    @Override
    public boolean onInit() {
        super.onInit();
        // data version
        fillData(new IntBytesConvertor(INetworkDefs.DATA_VERSION));
        // network cmd
        fillData(new IntBytesConvertor(mCmd));
        // write token length
        fillData(new IntBytesConvertor(mDevice.getAccessToken().length()));
        // write token
        fillData(new StringBytesConvertor(mDevice.getAccessToken()));
        return true;
    }
}
