package com.weichen2046.filesender2.service;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.networklib.NetworkAddressHelper;
import com.weichen2046.filesender2.utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * Created by chenwei on 12/4/16.
 */

public class PCDiscoverer extends IPCDiscoverer.Stub {

    private static final String TAG = "PCDiscoverer";

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
                DatagramSocket socket = null;
                try {
                    ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / 8 * 3);
                    bb.putInt(INetworkDefs.DATA_VERSION);
                    bb.putInt(INetworkDefs.CMD_PHONE_ONLINE);
                    bb.putInt(INetworkDefs.MOBILE_UDP_LISTEN_PORT);
                    byte[] buf = bb.array();
                    InetAddress group = InetAddress.getByName(broadcastAddress);
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, group, pcPort);
                    socket = new DatagramSocket();
                    socket.send(packet);
                    res = true;
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Utils.silenceClose(socket);
                }
                return res;
            }
        }.execute();
    }
}
