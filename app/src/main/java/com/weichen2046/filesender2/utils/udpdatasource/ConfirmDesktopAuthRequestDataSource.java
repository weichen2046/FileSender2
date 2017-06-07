package com.weichen2046.filesender2.utils.udpdatasource;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.utils.ByteDataSource;
import com.weichen2046.filesender2.utils.byteconvertor.BooleanBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.IntBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.StringBytesConvertor;

/**
 * Created by chenwei on 2017/3/20.
 */

public class ConfirmDesktopAuthRequestDataSource extends ByteDataSource {
    private Desktop mDesktop;
    private boolean mAccept;

    public ConfirmDesktopAuthRequestDataSource(Desktop desktop, boolean accept) {
        mDesktop = desktop;
        mAccept = accept;
    }

    @Override
    public boolean onInit() {
        super.onInit();
        // 4 bytes -> cmd version
        fillData(new IntBytesConvertor(INetworkDefs.DATA_VERSION));
        // 4 bytes -> cmd
        fillData(new IntBytesConvertor(INetworkDefs.CMD_T_CONFIRM_DESKTOP_AUTH_REQ));
        // 4 bytes -> access token length
        fillData(new IntBytesConvertor(mDesktop.getAccessToken().length()));
        // x bytes -> access token
        fillData(new StringBytesConvertor(mDesktop.getAccessToken()));
        // 1 bytes -> confirm state
        fillData(new BooleanBytesConvertor(mAccept));
        if (mAccept) {
            // 4 bytes -> auth token length
            fillData(new IntBytesConvertor(mDesktop.getAuthToken().length()));
            // x bytes -> auth token
            fillData(new StringBytesConvertor(mDesktop.getAuthToken()));
        }
        return true;
    }
}
