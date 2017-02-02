package com.weichen2046.filesender2.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;

import com.weichen2046.filesender2.db.FileSendingDataSource;
import com.weichen2046.filesender2.network.INetworkDefs;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by chenwei on 2017/2/2.
 */

public class FileSendUtils {
    private static final String TAG = "FileSendUtils";

    public static void sendFileToPc(Context context, Uri fileUri, String destHost, int destPort) {
        Socket socket = null;
        ParcelFileDescriptor pfd = null;
        Cursor cursor = null;
        try {
            String fileName = UUID.randomUUID().toString();
            long fileSize = 0;
            ContentResolver cr = context.getContentResolver();
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

            // write data version, 4 bytes
            ByteBuffer bBuf = ByteBuffer.allocate(Integer.SIZE / 8);
            bBuf.putInt(INetworkDefs.DATA_VERSION);
            bufOutputStream.write(bBuf.array());

            // write cmd, 4 bytes
            bBuf.rewind();
            bBuf.putInt(INetworkDefs.CMD_SEND_FILE);
            bufOutputStream.write(bBuf.array());

            // write file name length, 4 bytes
            bBuf.rewind();
            bBuf.putInt(nameBytes.length);
            bufOutputStream.write(bBuf.array());

            // write file name
            bufOutputStream.write(nameBytes);

            // write file content length, 8 bytes
            ByteBuffer bBufLong = ByteBuffer.allocate(Long.SIZE / 8);
            bBufLong.putLong(fileSize);
            bufOutputStream.write(bBufLong.array());

            // write file content
            pfd = cr.openFileDescriptor(fileUri, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            FileInputStream fis = new FileInputStream(fd);
            byte[] buf = new byte[2046];
            int read = fis.read(buf);
            while (read > 0) {
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

    public static void requestToSendFile(Context mContext, Uri fileUri, String destHost, int destPort) {
        Socket socket = null;
        ParcelFileDescriptor pfd = null;
        Cursor cursor = null;
        try {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            FileSendingDataSource dataSource = new FileSendingDataSource(mContext);
            dataSource.open();
            long id = dataSource.add(uuid, fileUri, destHost, destPort);
            dataSource.close();

            // use uuid as default file name
            String fileName = uuid;
            long fileSize = 0;

            ContentResolver cr = mContext.getContentResolver();
            // get file name
            cursor = cr.query(fileUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                fileSize = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
            }
            Log.d(TAG, "file to be sent: " + fileName + ", file size: " + fileSize + ", id:" + id
                    + ", uuid: " + uuid);
            byte[] nameBytes = fileName.getBytes();

            // connect to PC
            socket = new Socket(destHost, destPort);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            BufferedOutputStream bufOutputStream = new BufferedOutputStream(outputStream);

            // write data version, 4 bytes
            ByteBuffer bBuf = ByteBuffer.allocate(Integer.SIZE / 8);
            bBuf.putInt(INetworkDefs.DATA_VERSION);
            bufOutputStream.write(bBuf.array());

            // write cmd, 4 bytes
            bBuf.rewind();
            bBuf.putInt(INetworkDefs.CMD_SEND_FILE_REQ);
            bufOutputStream.write(bBuf.array());

            // write file name length, 4 bytes
            bBuf.rewind();
            bBuf.putInt(nameBytes.length);
            bufOutputStream.write(bBuf.array());

            // write file name
            bufOutputStream.write(nameBytes);

            // write file id
            ByteBuffer longBuf = ByteBuffer.allocate(Long.SIZE / 8);
            longBuf.putLong(id);
            bufOutputStream.write(longBuf.array());

            // write thumbnail indicate, 1 byte
            ByteBuffer bBufByte = ByteBuffer.allocate(Byte.SIZE / 8);
            bBufByte.put(INetworkDefs.HAS_NOT_THUMBNAIL);
            bufOutputStream.write(bBufByte.array());

            // TODO: write thumbnail total length
            // TODO: write thumbnail content

            bufOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Utils.silenceClose(cursor);
            Utils.silenceClose(pfd);
            Utils.silenceClose(socket);
        }
    }
}