package com.weichen2046.filesender2;

import android.app.Application;
import android.content.Context;

import com.weichen2046.filesender2.service.IServiceManager;

/**
 * Created by chenwei on 2017/1/31.
 */

public class MyApplication extends Application {

    private static MyApplication sInstance;
    private IServiceManager mServiceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public void setServiceManager(IServiceManager manager) {
        mServiceManager = manager;
    }

    public IServiceManager getServiceManager() {
       return mServiceManager;
    }
}
