package com.weichen2046.filesender2.service;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.weichen2046.filesender2.networkutils.NetworkAddressHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Created by chenwei on 12/4/16.
 */

public class PCDiscoverer extends IPCDiscoverer.Stub {

    private static final String TAG = "PCDiscoverer";

    private static final int DATA_VERSION = 1;

    @Override
    public void sayHello(int times, final int pcPort) throws RemoteException {
        // main thread can not have network operation
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean res = false;
                String broadcastAddress = NetworkAddressHelper.getBroadcastAddress();
                if (null == broadcastAddress) {
                    Log.w(TAG, "Can not get broadcast address");
                    return res;
                }

                // send broadcast to local network
                try {
                    ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / 8 * 2);
                    bb.putInt(DATA_VERSION);
                    bb.putInt(4555);
                    byte[] buf = bb.array();
                    InetAddress group = InetAddress.getByName(broadcastAddress);
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, group, pcPort);
                    DatagramSocket socket = new DatagramSocket();
                    socket.send(packet);
                    res = true;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return res;
            }
        }.execute();
    }
}
