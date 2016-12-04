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
import java.nio.ByteBuffer;

/**
 * Created by chenwei on 12/4/16.
 */

public class BroadcastMonitor extends IBroadcastMonitor.Stub {

    private static final String TAG = "BroadcastMonitor";
    private boolean mStared = false;

    private Thread mWorker = null;

    @Override
    public synchronized boolean start() throws RemoteException {
        if (mStared) {
            return true;
        }

        final String broadcastAddress = NetworkAddressHelper.getBroadcastAddress();
        if (null == broadcastAddress) {
            Log.w(TAG, "Can not get broadcast address");
            return false;
        }

        mWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "monitor thread start");
                DatagramSocket socket = null;
                DatagramPacket packet;
                try {
                    socket = new DatagramSocket(INetworkDefs.BROAD_MONITOR_LISTEN_PORT);
                    while (true) {
                        byte[] buf = new byte[256];
                        packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);

                        if (!mStared) {
                            break;
                        }

                        byte[] recvData = packet.getData();
                        Log.d(TAG, "recv data length: " + packet.getLength());
                        // 4 bytes -> version
                        // 4 bytes -> cmd
                        // other bytes -> cmd data
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (null != socket) {
                        socket.close();
                    }
                }
                Log.d(TAG, "monitor thread exit");
            }
        });
        mWorker.start();

        mStared = true;
        return true;
    }

    @Override
    public synchronized boolean stop() throws RemoteException {
        if (mStared) {
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    mStared = false;
                    DatagramSocket socket = null;
                    String broadcastAddress = NetworkAddressHelper.getBroadcastAddress();
                    if (null == broadcastAddress) {
                        Log.w(TAG, "Can not get broadcast address");
                        return false;
                    }

                    try {
                        ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / 8 * 2);
                        bb.putInt(INetworkDefs.DATA_VERSION);
                        bb.putInt(INetworkDefs.CMD_MAKE_PHONE_BROAD_MONITOR_EXIT);
                        byte[] buf = bb.array();
                        InetAddress group = InetAddress.getByName(broadcastAddress);
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, INetworkDefs.BROAD_MONITOR_LISTEN_PORT);
                        socket = new DatagramSocket();
                        socket.send(packet);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (null != socket) {
                            socket.close();
                        }
                    }
                    return true;
                }
            }.execute();
            return true;
        }
        return false;
    }
}
