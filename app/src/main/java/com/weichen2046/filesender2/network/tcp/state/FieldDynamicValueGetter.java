package com.weichen2046.filesender2.network.tcp.state;

import static org.joor.Reflect.*;

/**
 * Created by chenwei on 2017/4/30.
 */

public class FieldDynamicValueGetter<T> {
    private Object mHost;
    private String mMethod;

    public FieldDynamicValueGetter(Object host, String method) {
        mHost = host;
        mMethod = method;
    }
    public T getFieldValue() {
        return on(mHost).call(mMethod).get();
    }
}
