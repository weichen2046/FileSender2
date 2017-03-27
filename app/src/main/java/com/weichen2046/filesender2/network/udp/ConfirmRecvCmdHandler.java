package com.weichen2046.filesender2.network.udp;

import android.database.SQLException;
import android.os.RemoteException;
import android.util.Log;

import com.weichen2046.filesender2.MyApplication;
import com.weichen2046.filesender2.db.FileSendingDataSource;
import com.weichen2046.filesender2.db.FileSendingObj;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.IDesktopManager;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManager;
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
        ByteBuffer buffer = ByteBuffer.wrap(data.data);
        // read access token length
        int tokenLength = buffer.getInt();
        // read access token
        byte[] tokenBytes = new byte[tokenLength];
        buffer.get(tokenBytes);
        String authToken = new String(tokenBytes);

        Desktop desktop = findDesktop(data.addr.getHostAddress(), authToken);
        if (desktop == null) {
            Log.w(TAG, "desktop authenticate failed");
            return;
        }

        // read confirm state
        boolean confirmed = buffer.get() == 1;
        // read file id
        long fileId = buffer.getLong();
        Log.d(TAG, "confirm file id: " + fileId + ", confirmed: " + confirmed);

        mFileSendingDataSource = new FileSendingDataSource(MyApplication.getInstance());
        try {
            mFileSendingDataSource.open();
            FileSendingObj fsObj = mFileSendingDataSource.retrieve(fileId);
            if (fsObj == null) {
                return;
            }
            if (confirmed) {
                handleConfirm(fsObj, desktop);
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

    private Desktop findDesktop(String address, String authToken) {
        IServiceManager manager = get();
        Desktop desktop = null;
        try {
            IDesktopManager desktopManager =IDesktopManager.Stub.asInterface(
                    manager.getService(ServiceManager.SERVICE_DESKTOP_MANAGER));
            desktop = desktopManager.findDesktopByAuthToken(address, authToken);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return desktop;
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

    private void handleDenie(FileSendingObj fsObj) {
        deleteFileSendingObject(fsObj);
    }

    private void deleteFileSendingObject(FileSendingObj fsObj) {
        int del = mFileSendingDataSource.delete(fsObj);
        Log.d(TAG, "delete " + del + " row(s) by id " + (fsObj == null ? -1 : fsObj.id));
    }
}
