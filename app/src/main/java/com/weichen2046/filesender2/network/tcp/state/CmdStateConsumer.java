package com.weichen2046.filesender2.network.tcp.state;

/**
 * Created by chenwei on 2017/4/26.
 */

public class CmdStateConsumer extends IntStateConsumer {
    public CmdStateConsumer(StateConsumerCallback callback) {
        super(callback);
    }

    @Override
    public byte[] getAndResetRemains() {
        super.getAndResetRemains();
        return null;
    }
}
