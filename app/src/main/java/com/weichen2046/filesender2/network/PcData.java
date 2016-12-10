package com.weichen2046.filesender2.network;

import java.net.InetAddress;

/**
 * Created by chenwei on 12/10/16.
 */

public class PcData {
    public InetAddress addr;
    public int listenPort;
    public String name;

    public PcData(InetAddress addr, int listenPort) {
        this(addr, listenPort, null);
    }

    public PcData(InetAddress addr, int listenPort, String name) {
        this.addr = addr;
        this.listenPort = listenPort;
        if (null == name) {
            this.name = this.addr.getHostAddress();
        } else {
            this.name = name;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PcData)) {
            return false;
        }

        return this.addr.equals(((PcData) obj).addr);
    }
}
