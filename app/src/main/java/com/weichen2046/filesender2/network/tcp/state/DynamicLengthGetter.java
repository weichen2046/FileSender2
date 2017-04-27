package com.weichen2046.filesender2.network.tcp.state;

import static org.joor.Reflect.*;

/**
 * Created by chenwei on 2017/4/27.
 */

public class DynamicLengthGetter implements ILengthGetter {
    private Object mHost;
    private String mMethod;

    public DynamicLengthGetter(Object host, String method) {
        mHost = host;
        mMethod = method;
    }

    @Override
    public int getRequiredLength() {
        return on(mHost).call(mMethod).get();
    }
}
