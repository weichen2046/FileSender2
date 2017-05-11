package com.weichen2046.filesender2.utils.tcpdatasource;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.utils.byteconvertor.BooleanBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.IntBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.StringBytesConvertor;

/**
 * Created by chenwei on 2017/5/11.
 */

public class ConfirmSendingFileRequestDataSource extends AuthDataSource {
    private boolean mAccept;
    private String[] mFileIDs;

    public ConfirmSendingFileRequestDataSource(Desktop device, String[] fileIDs, boolean accept) {
        super(device, INetworkDefs.CMD_T_ACCEPT_SENDING_FILES);
        mAccept = accept;
        mFileIDs = fileIDs;
    }

    @Override
    public boolean onInit() {
        boolean res = super.onInit();
        if (!res) {
            return false;
        }

        fillData(new BooleanBytesConvertor(mAccept));

        if (!mAccept) {
            return true;
        }

        for (String fileId : mFileIDs) {
            // file id length
            fillData(new IntBytesConvertor(fileId.length()));
            // file id
            fillData(new StringBytesConvertor(fileId));
        }
        return true;
    }
}
