package com.weichen2046.filesender2.service;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.networklib.NetworkAddressHelper;
import com.weichen2046.filesender2.networklib.TokenHelper;
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

    private String mTempAccessToken;

    @Override
    public void sayHello(final String address, final int port) throws RemoteException {
        // main thread can not have network operation
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean res = false;
                String destAddress = address;
                if (TextUtils.isEmpty(destAddress)) {
                    destAddress =  NetworkAddressHelper.getBroadcastAddress();
                }
                if (TextUtils.isEmpty(destAddress)) {
                    Log.w(TAG, "Can not get destination address");
                    return res;
                }

                // send broadcast to local network
                DatagramSocket socket = null;
                try {
                    String token = getTempToken();
                    byte[] tokenBytes = token.getBytes();
                    ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / 8 * 4 + tokenBytes.length);
                    bb.putInt(INetworkDefs.DATA_VERSION);
                    bb.putInt(INetworkDefs.CMD_PHONE_ONLINE);
                    bb.putInt(tokenBytes.length);
                    bb.put(tokenBytes);
                    bb.putInt(INetworkDefs.MOBILE_UDP_LISTEN_PORT);
                    byte[] buf = bb.array();
                    InetAddress group = InetAddress.getByName(destAddress);
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);
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

    @Override
    public boolean checkTempAccessToken(String token) throws RemoteException {
        return mTempAccessToken.equals(token);
    }

    private String getTempToken() {
        if (null == mTempAccessToken) {
            mTempAccessToken = TokenHelper.generateTempToken();
        }
        Log.d(TAG, "temp access token: " + mTempAccessToken);
        return mTempAccessToken;
    }
}
