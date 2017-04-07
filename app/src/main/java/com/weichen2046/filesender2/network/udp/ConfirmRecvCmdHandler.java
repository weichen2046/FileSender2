package com.weichen2046.filesender2.network.udp;

import android.database.SQLException;
import android.util.Log;

import com.weichen2046.filesender2.MyApplication;
import com.weichen2046.filesender2.db.FileSendingDataSource;
import com.weichen2046.filesender2.db.FileSendingObj;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.SocketTaskService;

/**
 * Created by chenwei on 2017/1/31.
 */

public class ConfirmRecvCmdHandler extends UdpAuthCmdHandler {
    private FileSendingDataSource mFileSendingDataSource;

    public ConfirmRecvCmdHandler(int cmd) {
        super(cmd);
    }

    @Override
    public boolean handle(BroadcastData data) {
        boolean res = super.handle(data);
        if (!res) {
            return false;
        }

        // read confirm state
        boolean confirmed = mBuffer.get() == 1;
        // read file id
        long fileId = mBuffer.getLong();
        Log.d(TAG, "confirm file id: " + fileId + ", confirmed: " + confirmed);

        mFileSendingDataSource = new FileSendingDataSource(MyApplication.getInstance());
        try {
            mFileSendingDataSource.open();
            FileSendingObj fsObj = mFileSendingDataSource.retrieve(fileId);
            if (fsObj == null) {
                return false;
            }
            if (confirmed) {
                handleConfirm(fsObj, mDesktop);
            } else {
                handleDeny(fsObj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mFileSendingDataSource.close();
            mFileSendingDataSource = null;
        }
        return true;
    }

    private void handleConfirm(FileSendingObj fsObj, Desktop desktop) {
        Log.d(TAG, "confirm file sending obj: " + fsObj);
        if (!desktop.address.equals(fsObj.host)) {
            // TODO: abort send?
            Log.w(TAG, "desktop address not match, auth host: " + desktop.address + ", file host address: " + fsObj.host);
        }
        SocketTaskService.startActionSendFile(MyApplication.getInstance(), fsObj.getUri(), desktop);
        deleteFileSendingObject(fsObj);
    }

    private void handleDeny(FileSendingObj fsObj) {
        deleteFileSendingObject(fsObj);
    }

    private void deleteFileSendingObject(FileSendingObj fsObj) {
        int del = mFileSendingDataSource.delete(fsObj);
        Log.d(TAG, "delete " + del + " row(s) by id " + (fsObj == null ? -1 : fsObj.id));
    }
}
