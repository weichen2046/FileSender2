package com.weichen2046.filesender2.network.tcp;

/**
 * Created by chenwei on 2017/4/9.
 */

public interface ConsumerCallback {
    void onDataParsed(Object value, byte[] remains);
}
