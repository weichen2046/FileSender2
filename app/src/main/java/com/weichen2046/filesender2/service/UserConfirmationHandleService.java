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
import com.weichen2046.filesender2.ui.NotificationDialogHelperActivity;
import com.weichen2046.filesender2.ui.NotificationHelper;

import java.util.ArrayList;

public class UserConfirmationHandleService extends Service {
    private static final String TAG = "UserConfirmationHandleS";

    private static final int MSG_HANDLE_DELAY_TO_HANDLE = 1;
    private static final int MSG_HANDLE_AUTH_REQUEST = 2;
    private static final int MSG_HANDLE_RECV_FILE = 3;

    private static final String EXTRA_START_REQUEST_ID = "extra_start_request_id";
    public static final String EXTRA_ACCEPT_STATE = "extra_access_state";
    public static final String EXTRA_AUTH_DEVICE = "extra_auth_device";
    public static final String EXTRA_FILE_IDS = "extra_file_ids";
    public static final String EXTRA_FILE_NAMES = "extra_file_names";

    public static final String EXTRA_MSG_TYPE = "extra_msg_type";
    public static final int MSG_TYPE_AUTH       = 1;
    public static final int MSG_TYPE_RECV_FILE  = 2;

    private static final int MAX_DELAYED_MESSAGE_COUNT = 12;
    private ArrayList<Bundle> mDelayedMessagesForAuth = new ArrayList<>(MAX_DELAYED_MESSAGE_COUNT);
    private ArrayList<Bundle> mDelayedMessagesForFileRecv = new ArrayList<>(MAX_DELAYED_MESSAGE_COUNT);

    private IServiceManager mServiceManager;
    private HandlerThread mWorkThread;
    private Handler mHander;

    public UserConfirmationHandleService() {
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
        Bundle bundle = (Bundle) intent.getExtras().clone();
        bundle.putInt(EXTRA_START_REQUEST_ID, startId);

        int msgType = intent.getIntExtra(EXTRA_MSG_TYPE, 0);
        switch (msgType) {
            case MSG_TYPE_AUTH:
                nm.cancel(NotificationHelper.NOTIFICATION_DEVICE_AUTH_REQ);
                break;
            case MSG_TYPE_RECV_FILE:
                nm.cancel(NotificationHelper.NOTIFICATION_RECV_FILE_REQ);
                break;
            default:
                Log.w(TAG, "unknown msg type: " + msgType);
                break;
        }
        Message msg = mHander.obtainMessage(MSG_HANDLE_DELAY_TO_HANDLE, bundle);
        msg.sendToTarget();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "UserConfirmationHandleService onDestroy");
        super.onDestroy();
        // unbind IServiceManager
        if (mServiceManager != null) {
            unbindService(mConnection);
        }
        if (mHander == null) {
            mHander.getLooper().quit();
        }
    }

    private void acceptDeviceAuth(Desktop desktop) {
        try {
            // update desktop
            IDesktopManager desktopManager = IDesktopManager.Stub.asInterface(
                    mServiceManager.getService(ServiceManager.SERVICE_DESKTOP_MANAGER));
            desktopManager.updateDesktop(desktop);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // send auth request confirm message to remote device
        // send auth token back and use access token to auth with remote device
        SocketTaskService.confirmDesktopAuthRequest(MyApplication.getInstance(), desktop, true);
    }

    private void denialDeviceAuth(Desktop desktop) {
        try {
            // delete from desktop manager
            IDesktopManager desktopManager = IDesktopManager.Stub.asInterface(
                    mServiceManager.getService(ServiceManager.SERVICE_DESKTOP_MANAGER));
            desktopManager.deleteDesktop(desktop);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // send auth request denialDeviceAuth message to desktop
        SocketTaskService.confirmDesktopAuthRequest(MyApplication.getInstance(), desktop, false);
    }

    private void acceptFileSendingRequest(Desktop device, String[] fileIDs, String[] fileNames) {
        SocketTaskService.confirmFileSendingRequest(MyApplication.getInstance(), device, fileIDs, true);
    }

    private void denialFileSendingRequest(Desktop device, String[] fileIDs) {
        SocketTaskService.confirmFileSendingRequest(MyApplication.getInstance(), device, fileIDs, false);
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

    private ArrayList<Bundle> getDelayMsgList(int msgType) {
        ArrayList<Bundle> list = null;
        switch (msgType) {
            case MSG_TYPE_AUTH:
                list = mDelayedMessagesForAuth;
                break;
            case MSG_TYPE_RECV_FILE:
                list = mDelayedMessagesForFileRecv;
                break;
        }
        return list;
    }

    private int getMsgId(int msgType) {
        int msgId = -1;
        switch (msgType) {
            case MSG_TYPE_AUTH:
                msgId = MSG_HANDLE_AUTH_REQUEST;
                break;
            case MSG_TYPE_RECV_FILE:
                msgId = MSG_HANDLE_RECV_FILE;
                break;
        }
        return msgId;
    }

    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int lastReqId = -1;
            switch (msg.what) {
                case MSG_HANDLE_AUTH_REQUEST:
                    // send broadcast to dismiss NotificationDialogHelperActivity for auth request
                    MyApplication.getInstance().sendBroadcast(
                            new Intent(NotificationDialogHelperActivity.ACTION_DISMISS_NOTIFICATION_DIALOG));
                    if (mServiceManager == null) {
                        return;
                    }
                    if (mDelayedMessagesForAuth.size() > 0) {
                        for (Bundle bundle : mDelayedMessagesForAuth) {
                            lastReqId = bundle.getInt(EXTRA_START_REQUEST_ID);
                            boolean accept = bundle.getBoolean(EXTRA_ACCEPT_STATE);
                            Desktop desktop = bundle.getParcelable(EXTRA_AUTH_DEVICE);
                            Log.d(TAG, (accept ? "accept" : "denial") + " authentication request from " + desktop);
                            if (accept) {
                                desktop.setAuthToken(TokenHelper.generateToken());
                                //Log.d(TAG, "generate new auth token for device: " + desktop);
                                acceptDeviceAuth(desktop);
                            } else {
                                denialDeviceAuth(desktop);
                            }
                        }
                        mDelayedMessagesForAuth.clear();
                        stopSelfResult(lastReqId);
                    }
                    break;

                case MSG_HANDLE_RECV_FILE:
                    for (Bundle bundle : mDelayedMessagesForFileRecv) {
                        lastReqId = bundle.getInt(EXTRA_START_REQUEST_ID);
                        boolean accept = bundle.getBoolean(EXTRA_ACCEPT_STATE);
                        Desktop device = bundle.getParcelable(EXTRA_AUTH_DEVICE);
                        String[] fileIDs = bundle.getStringArray(EXTRA_FILE_IDS);
                        String[] fileNames = bundle.getStringArray(EXTRA_FILE_NAMES);
                        Log.d(TAG, (accept ? "accept" : "denial") + " send file request from " + device);
                        if (accept) {
                            acceptFileSendingRequest(device, fileIDs, fileNames);
                        } else {
                            denialFileSendingRequest(device, fileIDs);
                        }
                    }
                    mDelayedMessagesForFileRecv.clear();
                    stopSelfResult(lastReqId);
                    break;

                case MSG_HANDLE_DELAY_TO_HANDLE:
                    Bundle bundle = (Bundle) msg.obj;
                    int msgType = bundle.getInt(EXTRA_MSG_TYPE);
                    ArrayList<Bundle> targetList = getDelayMsgList(msgType);
                    if (targetList != null) {
                        if (targetList.size() < MAX_DELAYED_MESSAGE_COUNT) {
                            targetList.add(bundle);
                            int msgId = getMsgId(msgType);
                            Message msgx = mHander.obtainMessage(msgId);
                            msgx.sendToTarget();
                        } else {
                            Log.w(TAG, "delayed message full, ignore request for msg type: " + msgType);
                        }
                    } else {
                        lastReqId = bundle.getInt(EXTRA_START_REQUEST_ID);
                        stopSelfResult(lastReqId);
                    }
                    break;
            }
        }
    }
}
