package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/30.
 */

public class UnknownIntLengthGetter implements IIntLengthGetter {
    @Override
    public int getLength() {
        return UNKNOWN_LENGTH;
    }
}
