package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/30.
 */

public class DynamicLongLengthGetter extends  FieldDynamicValueGetter<Long> implements ILongLengthGetter {
    public DynamicLongLengthGetter(Object host, String method) {
        super(host, method);
    }

    @Override
    public long getLength() {
        return super.getFieldValue();
    }
}
