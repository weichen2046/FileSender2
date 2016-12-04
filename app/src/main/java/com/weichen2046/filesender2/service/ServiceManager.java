package com.weichen2046.filesender2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;

public class ServiceManager extends Service {

    private static final String TAG = "ServiceManager";

    private HashMap<Integer, IBinder> mSubServices = new HashMap<>();

    public static final int SERVICE_PC_DISCOVERER = 1;

    public ServiceManager() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IBinder mBinder = new IServiceManager.Stub() {
        @Override
        public IBinder getService(int serviceId) throws RemoteException {
            if (mSubServices.containsKey(serviceId)) {
                return mSubServices.get(serviceId);
            }

            if (serviceId == SERVICE_PC_DISCOVERER) {
                IBinder binder = new PCDiscoverer();
                mSubServices.put(SERVICE_PC_DISCOVERER, binder);
                return binder;
            }

            return null;
        }
    };
}
