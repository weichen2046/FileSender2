package com.weichen2046.filesender2.service;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;

/**
 * Created by chenwei on 2016/12/17.
 */

public class DataTransfer extends IDataTransfer.Stub {

    private static final String TAG = "DataTransfer";

    private Context mContext;

    public DataTransfer(Context context) {
        mContext = context;
    }

    /**
     * Send file to PC specified by the destHost and destPort.
     * This call will use a TCP connection to send the file to PC.
     *
     * @param fileUri The uri of the file to be sent.
     * @param destHost The destination PC host address in String format.
     * @param destPort The destination PC listen port.
     * @throws RemoteException
     */
    @Override
    public void sendFileToPc(Uri fileUri, String destHost, int destPort) throws RemoteException {
        SendFileService.startActionSendFile(mContext, fileUri, destHost, destPort);
    }

    @Override
    public void requestToSendFile(Uri fileUri, String destHost, int destPort) throws RemoteException {
        SendFileService.startActionRequestSendFile(mContext, fileUri, destHost, destPort);
    }
}
