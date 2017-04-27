package com.weichen2046.filesender2.service;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncServerSocket;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;
import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.network.tcp.state.StateConsumer;
import com.weichen2046.filesender2.network.tcp.TcpDataConsumer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by chenwei on 2017/4/9.
 */

public class TcpDataMonitor extends ITcpDataMonitor.Stub {
    private static final String TAG = "TcpDataMonitor";

    private boolean mStarted = false;

    private ArrayList<AsyncSocket> mClients = new ArrayList<>();

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
                    // listen on all addresses
                    InetAddress host = InetAddress.getByName("0.0.0.0");
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
        clearClients();
        return true;
    }

    private synchronized void addClient(AsyncSocket client) {
        mClients.add(client);
    }

    private synchronized void removeClient(AsyncSocket client) {
        mClients.remove(client);
    }

    private synchronized void clearClients() {
        mClients.clear();
    }

    private ListenCallback mListenCallback = new ListenCallback() {
        @Override
        public void onListening(AsyncServerSocket socket) {
            Log.d(TAG, "Tcp data monitor started");
        }

        @Override
        public void onAccepted(final AsyncSocket socket) {
            Log.d(TAG, "accept tcp connection, socket: " + socket);
            addClient(socket);
            final TcpDataConsumer consumer = new TcpDataConsumer();
            consumer.init(-1, -1, null);
            socket.setDataCallback(new DataCallback() {
                @Override
                public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                    byte[] data = bb.getAllByteArray();
                    Log.d(TAG, "on client data available, bytes: " + data.length);
                    StateConsumer.HandleState res = consumer.handle(data);
                    while (res == StateConsumer.HandleState.OK) {
                        res = consumer.handle(null);
                    }
                    if (res == StateConsumer.HandleState.FAIL) {
                        socket.end();
                        socket.close();
                    }
                }
            });

            socket.setClosedCallback(new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    Log.d(TAG, "on client closed");
                    removeClient(socket);
                }
            });
        }

        @Override
        public void onCompleted(Exception ex) {
            Log.d(TAG, "Tcp data monitor shutdown");
        }
    };
}
