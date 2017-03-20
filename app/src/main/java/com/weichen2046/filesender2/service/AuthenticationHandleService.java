package com.weichen2046.filesender2.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.weichen2046.filesender2.MyApplication;
import com.weichen2046.filesender2.networklib.TokenHelper;
import com.weichen2046.filesender2.utils.NotificationHelper;

import java.util.ArrayList;

public class AuthenticationHandleService extends Service {
    private static final String TAG = "AuthenticationHandle";

    private static final int MSG_HANDLE_AUTH_REQUEST = 1;
    private static final int MSG_HANDLE_ADD_DELAY_AUTH_REQUEST = 2;

    private static final String EXTRA_START_REQUEST_ID = "extra_start_request_id";
    public static final String EXTRA_ACCEPT_STATE = "extra_access_state";
    public static final String EXTRA_AUTH_DESKTOP = "extra_auth_desktop";

    private static final int MAX_DELAYED_MESSAGE_COUNT = 12;
    private ArrayList<Bundle> mDelayedMessages = new ArrayList<>(MAX_DELAYED_MESSAGE_COUNT);

    private IServiceManager mServiceManager;
    private HandlerThread mWorkThread;
    private Handler mHander;

    public AuthenticationHandleService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // start bind IServiceManager
        if (mServiceManager == null) {
            Intent service = new Intent(this, ServiceManager.class);
            bindService(service, mConnection, Context.BIND_AUTO_CREATE);
        }

        mWorkThread = new HandlerThread("Auth-Handle-Service");
        mWorkThread.start();
        mHander = new MyHandler(mWorkThread.getLooper());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NotificationHelper.NOTIFICATION_DESKTOP_AUTH_REQ);
        boolean accept = intent.getBooleanExtra(EXTRA_ACCEPT_STATE, false);
        Desktop desktop = intent.getParcelableExtra(EXTRA_AUTH_DESKTOP);

        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_ACCEPT_STATE, accept);
        bundle.putParcelable(EXTRA_AUTH_DESKTOP, desktop);
        bundle.putInt(EXTRA_START_REQUEST_ID, startId);

        Message msg = mHander.obtainMessage(MSG_HANDLE_ADD_DELAY_AUTH_REQUEST, bundle);
        msg.sendToTarget();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "AuthenticationHandleService onDestroy");
        super.onDestroy();
        // unbind IServiceManager
        if (mServiceManager != null) {
            unbindService(mConnection);
        }
        if (mHander == null) {
            mHander.getLooper().quit();
        }
    }

    private void accept(Desktop desktop) {
        try {
            // update desktop
            IDesktopManager desktopManager = IDesktopManager.Stub.asInterface(
                    mServiceManager.getService(ServiceManager.SERVICE_DESKTOP_MANAGER));
            desktopManager.updateDesktop(desktop);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // send auth request confirm message to desktop
        // send auth token back and use access token to auth with desktop
        SocketTaskService.confirmDesktopAuthRequest(MyApplication.getInstance(), desktop, true);
    }

    private void denial(Desktop desktop) {
        try {
            // delete from desktop manager
            IDesktopManager desktopManager = IDesktopManager.Stub.asInterface(
                    mServiceManager.getService(ServiceManager.SERVICE_DESKTOP_MANAGER));
            desktopManager.deleteDesktop(desktop);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // send auth request denial message to desktop
        SocketTaskService.confirmDesktopAuthRequest(MyApplication.getInstance(), desktop, false);
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceManager = IServiceManager.Stub.asInterface(service);
            Message msg = mHander.obtainMessage(MSG_HANDLE_AUTH_REQUEST);
            msg.sendToTarget();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceManager = null;
        }
    };

    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HANDLE_AUTH_REQUEST:
                    if (mServiceManager == null) {
                        return;
                    }
                    if (mDelayedMessages.size() > 0) {
                        int lastReqId = -1;
                        for (Bundle bundle : mDelayedMessages) {
                            lastReqId = bundle.getInt(EXTRA_START_REQUEST_ID);
                            boolean accept = bundle.getBoolean(EXTRA_ACCEPT_STATE);
                            Desktop desktop = bundle.getParcelable(EXTRA_AUTH_DESKTOP);
                            Log.d(TAG, (accept ? "accept" : "denial") + " authentication request from " + desktop);
                            if (accept) {
                                desktop.authToken = TokenHelper.generateToken();
                                accept(desktop);
                            } else {
                                denial(desktop);
                            }
                        }
                        mDelayedMessages.clear();
                        stopSelfResult(lastReqId);
                    }
                    break;
                case MSG_HANDLE_ADD_DELAY_AUTH_REQUEST:
                    if (mDelayedMessages.size() < MAX_DELAYED_MESSAGE_COUNT) {
                        mDelayedMessages.add((Bundle) msg.obj);
                        Message msgx = mHander.obtainMessage(MSG_HANDLE_AUTH_REQUEST);
                        msgx.sendToTarget();
                    } else {
                        Log.w(TAG, "delayed message full, ignore authentication request");
                    }
                    break;
            }
        }
    }
}
