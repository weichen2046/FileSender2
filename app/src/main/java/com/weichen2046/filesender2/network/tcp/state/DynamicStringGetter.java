package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/30.
 */

public class DynamicStringGetter extends FieldDynamicValueGetter<String> implements IStringGetter {
    public DynamicStringGetter(Object host, String method) {
        super(host, method);
    }

    @Override
    public String getString() {
        return super.getFieldValue();
    }
}
