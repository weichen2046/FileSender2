package com.weichen2046.filesender2.service;

import android.os.AsyncTask;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.weichen2046.filesender2.network.udp.BroadcastCmdDispatcher;
import com.weichen2046.filesender2.network.udp.BroadcastData;
import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.networklib.NetworkAddressHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by chenwei on 12/4/16.
 */

public class BroadcastMonitor extends IBroadcastMonitor.Stub implements IServiceManagerHolder {

    private static final String TAG = "BroadcastMonitor";
    private boolean mStared = false;

    private IServiceManager mServiceManager;

    private Thread mWorker = null;
    private HandlerThread mCmdThread = null;
    private BroadcastCmdDispatcher mCmdDispatcher = null;

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

        mCmdThread = new HandlerThread("broadcast-cmd-thread");
        mCmdThread.start();
        mCmdDispatcher = new BroadcastCmdDispatcher(mCmdThread.getLooper());
        mCmdDispatcher.attach(mServiceManager);

        mWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "monitor thread start");
                DatagramSocket socket = null;
                DatagramPacket packet;
                try {
                    socket = new DatagramSocket(INetworkDefs.MOBILE_UDP_LISTEN_PORT);
                    while (true) {
                        byte[] buf = new byte[256];
                        packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);

                        if (!mStared) {
                            break;
                        }

                        int dataLength = packet.getLength();
                        if (dataLength < INetworkDefs.MIN_DATA_LENGTH) {
                            Log.d(TAG, "recv data length less than 8 bytes, ignore");
                            continue;
                        }

                        Log.d(TAG, "recv data length: " + dataLength);
                        // 4 bytes -> version
                        ByteBuffer recvBuf = ByteBuffer.wrap(buf, 0, dataLength);
                        int version = recvBuf.getInt();
                        Log.d(TAG, "recv data version: " + version);
                        // 4 bytes -> cmd
                        int cmd = recvBuf.getInt();
                        Log.d(TAG, "recv data cmd: " + cmd);
                        // other bytes -> cmd data
                        byte[] cmdData = null;
                        if (dataLength > INetworkDefs.MIN_DATA_LENGTH) {
                            cmdData = Arrays.copyOfRange(buf, INetworkDefs.MIN_DATA_LENGTH,
                                    dataLength);
                        }
                        BroadcastData bd = new BroadcastData(packet.getAddress(), packet.getPort(),
                                cmdData);
                        Message msg = mCmdDispatcher.obtainMessage(cmd, bd);
                        msg.sendToTarget();
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
                        bb.putInt(INetworkDefs.CMD_PHONE_OFFLINE);
                        byte[] buf = bb.array();
                        InetAddress group = InetAddress.getByName(broadcastAddress);
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, INetworkDefs.MOBILE_UDP_LISTEN_PORT);
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
            mCmdDispatcher.detach();
            mCmdThread.getLooper().quit();
            return true;
        }
        return false;
    }

    @Override
    public void attach(IServiceManager manager) {
        mServiceManager = manager;
    }

    @Override
    public void detach() {
        mServiceManager = null;
    }

    @Override
    public IServiceManager get() {
        return mServiceManager;
    }
}
