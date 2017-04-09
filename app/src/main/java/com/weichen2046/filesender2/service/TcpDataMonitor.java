package com.weichen2046.filesender2.service;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncServerSocket;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.callback.ListenCallback;
import com.weichen2046.filesender2.network.INetworkDefs;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by chenwei on 2017/4/9.
 */

public class TcpDataMonitor extends ITcpDataMonitor.Stub {
    private static final String TAG = "TcpDataMonitor";

    private boolean mStarted = false;

    @Override
    public boolean start() throws RemoteException {
        if (mStarted) {
            return true;
        }
        mStarted = true;
        // network operation should not in main thread
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    InetAddress host = InetAddress.getByName("localhost");
                    AsyncServer.getDefault().listen(host, INetworkDefs.DEFAULT_MOBILE_TCP_PORT, mListenCallback);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
        return true;
    }

    @Override
    public boolean stop() throws RemoteException {
        if (!mStarted) {
            return false;
        }
        mStarted = false;
        AsyncServer.getDefault().stop();
        return true;
    }

    private ListenCallback mListenCallback = new ListenCallback() {
        @Override
        public void onListening(AsyncServerSocket socket) {
            Log.d(TAG, "Tcp data monitor started");
        }

        @Override
        public void onAccepted(AsyncSocket socket) {
            Log.d(TAG, "accept tcp connection, socket: " + socket);
        }

        @Override
        public void onCompleted(Exception ex) {
            Log.d(TAG, "Tcp data monitor shutdown");
        }
    };
}
