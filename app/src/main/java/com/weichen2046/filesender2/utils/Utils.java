package com.weichen2046.filesender2.utils;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;

/**
 * Created by chenwei on 2016/12/17.
 */

public class Utils {

    public static void silenceClose(Closeable obj) {
        if (null != obj) {
            try {
                obj.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void silenceClose(Socket socket) {
        if (null != socket) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void silenceClose(DatagramSocket socket) {
        if (null != socket) {
            socket.close();
        }
    }
}