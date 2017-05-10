package com.weichen2046.filesender2.service;

/**
 * Created by chenwei on 2017/3/18.
 */

public class ServiceManagerHolder implements IServiceManagerHolder {
    private  IServiceManager mServiceManager;

    @Override
    public void attach(IServiceManager manager) {
        mServiceManager = manager;
    }

    @Override
    public void detach() {
        mServiceManager = null;
    }

    @Override
    public IServiceManager getServiceManager() {
        return mServiceManager;
    }
}
