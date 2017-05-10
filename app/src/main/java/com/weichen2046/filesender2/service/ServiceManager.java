package com.weichen2046.filesender2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;

import com.weichen2046.filesender2.utils.TcpDataSender;

public class ServiceManager extends Service {

    private static final String TAG = "ServiceManager";

    private SparseArray<IBinder> mSubServices = new SparseArray<>();

    public static final int SERVICE_DESKTOP_DISCOVERER  = 1;
    public static final int SERVICE_UDP_DATA_MONITOR    = 2;
    public static final int SERVICE_TCP_DATA_MONITOR    = 3;
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
                if (serviceId == SERVICE_DESKTOP_DISCOVERER) {
                    binder = new DesktopDiscoverer();
                    mSubServices.put(SERVICE_DESKTOP_DISCOVERER, binder);
                }

                if (serviceId == SERVICE_UDP_DATA_MONITOR) {
                    UdpDataMonitor udpDataMonitor = new UdpDataMonitor();
                    udpDataMonitor.attach(IServiceManager.Stub.asInterface(this));
                    binder = udpDataMonitor;
                    mSubServices.put(SERVICE_UDP_DATA_MONITOR, binder);
                }

                if (serviceId == SERVICE_TCP_DATA_MONITOR) {
                    TcpDataMonitor tcpDataMonitor = new TcpDataMonitor();
                    tcpDataMonitor.attach(IServiceManager.Stub.asInterface(this));
                    binder = tcpDataMonitor;
                    mSubServices.put(SERVICE_TCP_DATA_MONITOR, binder);
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
