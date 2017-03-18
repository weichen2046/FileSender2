package com.weichen2046.filesender2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;

public class ServiceManager extends Service {

    private static final String TAG = "ServiceManager";

    private SparseArray<IBinder> mSubServices = new SparseArray<>();

    public static final int SERVICE_PC_DISCOVERER       = 1;
    public static final int SERVICE_BROADCAST_MONITOR   = 2;
    public static final int SERVICE_DATA_TRANSFER       = 3;
    public static final int SERVICE_DESKTOP_MANAGER     = 4;

    public ServiceManager() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IBinder mBinder = new IServiceManager.Stub() {
        @Override
        public synchronized IBinder getService(int serviceId) throws RemoteException {
            IBinder binder = mSubServices.get(serviceId);
            if (binder == null) {
                if (serviceId == SERVICE_PC_DISCOVERER) {
                    binder = new PCDiscoverer();
                    mSubServices.put(SERVICE_PC_DISCOVERER, binder);
                }

                if (serviceId == SERVICE_BROADCAST_MONITOR) {
                    BroadcastMonitor broadcastMonitor = new BroadcastMonitor();
                    broadcastMonitor.attach(IServiceManager.Stub.asInterface(this));
                    binder = broadcastMonitor;
                    mSubServices.put(SERVICE_BROADCAST_MONITOR, binder);
                }

                if (serviceId == SERVICE_DATA_TRANSFER) {
                    binder = new DataTransfer(ServiceManager.this);
                    mSubServices.put(SERVICE_DATA_TRANSFER, binder);
                }

                if (serviceId == SERVICE_DESKTOP_MANAGER) {
                    binder = new DesktopManager();
                    mSubServices.put(SERVICE_DESKTOP_MANAGER, binder);
                }
            }
            return binder;
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called.");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind called.");
        return super.onUnbind(intent);
    }
}
