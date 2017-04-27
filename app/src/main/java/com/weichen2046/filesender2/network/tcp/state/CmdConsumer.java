package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/26.
 */

public class CmdConsumer extends IntConsumer {
    public CmdConsumer(ConsumerCallback callback) {
        super(callback);
    }
}
