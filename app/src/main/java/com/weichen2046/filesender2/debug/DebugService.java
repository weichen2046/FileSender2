package com.weichen2046.filesender2.debug;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.IRemoteDevicesManager;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManager;

import java.util.List;

public class DebugService extends Service {
    private static final String TAG = "DebugService";

    private static final String ACTION_CMD = "com.weichen2046.filesender2.debugservice";
    private static final String EXTRA_CMD = "extra_cmd";

    private static final String CMD_DUMP_DESKTOPS = "cmd_dump_desktops";

    private IServiceManager mManager;

    public DebugService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent service = new Intent(this, ServiceManager.class);
        bindService(service, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mManager != null) {
            unbindService(mConnection);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mManager = IServiceManager.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mManager = null;
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String action = intent.getAction();
        if (!ACTION_CMD.equals(action)) {
            return super.onStartCommand(intent, flags, startId);
        }
        String cmd = intent.getStringExtra(EXTRA_CMD);
        if (TextUtils.isEmpty(cmd)) {
            return super.onStartCommand(intent, flags, startId);
        }
        if (mManager == null) {
            Log.d(TAG, "service manager not connected");
            return super.onStartCommand(intent, flags, startId);
        }

        handleCmd(cmd, intent.getExtras());
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleCmd(String cmd, Bundle params) {
        switch (cmd) {
            case CMD_DUMP_DESKTOPS:
                handleDumpDesktops();
                break;
            default:
                Log.d(TAG, "unknown cmd: " + cmd);
                break;
        }
    }

    private void handleDumpDesktops() {
        try {
            IRemoteDevicesManager desktopManager =  IRemoteDevicesManager.Stub.asInterface(
                    mManager.getService(ServiceManager.SERVICE_DEVICES_MANAGER));
            List<Desktop> desktops = desktopManager.getAllDesktops();
            StringBuffer buffer = new StringBuffer();
            buffer.append("=================== Dump desktops ===================").append("\n");
            buffer.append("Desktop size: ").append(desktops.size()).append("\n");
            int index = 0;
            for (Desktop d : desktops) {
                buffer.append(index).append(" ").append(d).append("\n");
                index++;
            }
            buffer.append("===================      End      ===================").append("\n");
            Log.d(TAG, buffer.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
