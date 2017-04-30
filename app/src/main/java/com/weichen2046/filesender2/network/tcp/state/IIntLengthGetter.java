package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/30.
 */

public interface IIntLengthGetter {
    int UNKNOWN_LENGTH = -1;

    int getLength();
}
