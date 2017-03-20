package com.weichen2046.filesender2.utils;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by chenwei on 2017/3/20.
 */

public class UdpDataSender {
    private static final String TAG = "UdpDataSender";

    public static void sendData(String destHost, int destPort, ByteDataSource source) {
        DatagramSocket socket = null;
        try {
            boolean res = source.init();
            if (!res) {
                Log.w(TAG, "data source init failed, ignore send tcp data");
                return;
            }
            byte[] buf = source.getAllData();
            Log.d(TAG, "udp data length: " + buf.length);
            if (buf != null) {
                InetAddress host = InetAddress.getByName(destHost);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, host, destPort);
                socket = new DatagramSocket();
                socket.send(packet);
            } else {
                Log.w(TAG, "data buffer is null");
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.silenceClose(socket);
        }
    }
}
