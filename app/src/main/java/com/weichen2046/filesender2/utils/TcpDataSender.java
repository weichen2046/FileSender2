package com.weichen2046.filesender2.utils;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by chenwei on 2017/2/4.
 */

public class TcpDataSender {
    private static final String TAG = "TcpDataSender";

    public static void sendDataSync(String destHost, int destPort, ByteDataSource source) {
        Socket socket = null;
        try {
            boolean res = source.init();
            if (!res) {
                Log.d(TAG, "Data source init failed, ignore send tcp data");
                return;
            }
            socket = new Socket(destHost, destPort);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            byte[] data = source.getData();
            while (data != null) {
                bufferedOutputStream.write(data);
                data = source.getData();
            }
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.silenceClose(socket);
            source.destroy();
        }
    }
}
