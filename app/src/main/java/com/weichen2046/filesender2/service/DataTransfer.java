package com.weichen2046.filesender2.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.OpenableColumns;
import android.util.Log;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.UUID;


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
        Socket socket = null;
        ParcelFileDescriptor pfd = null;
        Cursor cursor = null;
        try {
            String fileName = UUID.randomUUID().toString();
            long fileSize = 0;
            ContentResolver cr = mContext.getContentResolver();
            // get file name
            cursor = cr.query(fileUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                fileSize = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
            }
            Log.d(TAG, "file to be sent: " + fileName + ", file size: " + fileSize);
            byte[] nameBytes = fileName.getBytes();

            // connect to PC
            socket = new Socket(destHost, destPort);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            BufferedOutputStream bufOutputStream = new BufferedOutputStream(outputStream);

            // write data length
            ByteBuffer bBuf = ByteBuffer.allocate(Integer.SIZE / 8);
            bBuf.putInt(16 + nameBytes.length);
            bufOutputStream.write(bBuf.array());

            // write network data version, 4 bytes
            bBuf.rewind();
            bBuf.putInt(INetworkDefs.DATA_VERSION);
            bufOutputStream.write(bBuf.array());

            // write cmd, 4 bytes
            bBuf.rewind();
            bBuf.putInt(INetworkDefs.CMD_SEND_FILE);
            bufOutputStream.write(bBuf.array());

            // send file size, 8 bytes
            ByteBuffer bBufLong = ByteBuffer.allocate(Long.SIZE / 8);
            bBufLong.putLong(fileSize);
            bufOutputStream.write(bBufLong.array());

            // send file name
            bufOutputStream.write(nameBytes);

            // send file content
            pfd = cr.openFileDescriptor(fileUri, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            FileInputStream fis = new FileInputStream(fd);
            byte[] buf = new byte[2046];
            int read = fis.read(buf);
            while (read > 0) {
                bBuf.rewind();
                bBuf.putInt(read);
                bufOutputStream.write(bBuf.array());
                bufOutputStream.write(buf, 0, read);
                read = fis.read(buf);
            }
            bufOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.silenceClose(cursor);
            Utils.silenceClose(pfd);
            Utils.silenceClose(socket);
        }
    }
}
