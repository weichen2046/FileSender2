package com.weichen2046.filesender2.service;

/**
 * Created by chenwei on 2017/3/18.
 */

public interface IServiceManagerHolder {
    void attach(IServiceManager manager);
    void detach();
    IServiceManager getServiceManager();
}
