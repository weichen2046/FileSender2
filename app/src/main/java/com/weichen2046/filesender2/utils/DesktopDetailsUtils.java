package com.weichen2046.filesender2.utils;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by chenwei on 2017/2/4.
 */

public class DesktopDetailsUtils {
    public static void requestDesktopDetails(Context context, String destHost, int destPort) {
        Socket socket = null;
        try {
            // connect to desktop machine
            socket = new Socket(destHost, destPort);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            BufferedOutputStream bufOutputStream = new BufferedOutputStream(outputStream);

            // TODO: write bytes to bufOutputStream

            bufOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.silenceClose(socket);
        }
    }
}
