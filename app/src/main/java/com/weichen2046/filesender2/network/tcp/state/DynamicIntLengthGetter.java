package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/30.
 */

public class DynamicIntLengthGetter extends FieldDynamicValueGetter<Integer> implements IIntLengthGetter {
    public DynamicIntLengthGetter(Object host, String method) {
        super(host, method);
    }

    @Override
    public int getLength() {
        return super.getFieldValue();
    }
}
