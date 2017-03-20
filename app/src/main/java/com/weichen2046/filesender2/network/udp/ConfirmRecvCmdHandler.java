package com.weichen2046.filesender2.network.udp;

import android.database.SQLException;
import android.util.Log;

import com.weichen2046.filesender2.MyApplication;
import com.weichen2046.filesender2.db.FileSendingDataSource;
import com.weichen2046.filesender2.db.FileSendingObj;
import com.weichen2046.filesender2.service.SocketTaskService;

import java.nio.ByteBuffer;

/**
 * Created by chenwei on 2017/1/31.
 */

public class ConfirmRecvCmdHandler extends UdpCmdHandler {
    private FileSendingDataSource mFileSendingDataSource;

    public ConfirmRecvCmdHandler(int cmd) {
        super(cmd);
    }

    @Override
    public void handle(BroadcastData data) {
        ByteBuffer bb = ByteBuffer.wrap(data.data);
        boolean confirmed = bb.get() == 1;
        long fileId = bb.getLong();
        Log.d(TAG, "confirm file id: " + fileId + ", confirmed: " + confirmed);

        mFileSendingDataSource = new FileSendingDataSource(MyApplication.getInstance());
        try {
            mFileSendingDataSource.open();
            FileSendingObj fsObj = mFileSendingDataSource.retrieve(fileId);
            if (fsObj == null) {
                return;
            }
            if (confirmed) {
                handleConfirm(fsObj);
            } else {
                handleDenie(fsObj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mFileSendingDataSource.close();
            mFileSendingDataSource = null;
        }
    }

    private void handleConfirm(FileSendingObj fsObj) {
        Log.d(TAG, "confirm file sending obj: " + fsObj);
        SocketTaskService.startActionSendFile(MyApplication.getInstance(), fsObj.getUri(), fsObj.host,
                fsObj.port);
        deleteFileSendingObject(fsObj);
    }

    private void handleDenie(FileSendingObj fsObj) {
        deleteFileSendingObject(fsObj);
    }

    private void deleteFileSendingObject(FileSendingObj fsObj) {
        int del = mFileSendingDataSource.delete(fsObj);
        Log.d(TAG, "delete " + del + " row(s) by id " + (fsObj == null ? -1 : fsObj.id));
    }
}
