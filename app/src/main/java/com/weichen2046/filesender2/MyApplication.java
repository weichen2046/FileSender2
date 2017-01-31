package com.weichen2046.filesender2;

import android.app.Application;
import android.content.Context;

/**
 * Created by chenwei on 2017/1/31.
 */

public class MyApplication extends Application {

    private static Context sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static Context getInstance() {
        return sInstance;
    }
}
