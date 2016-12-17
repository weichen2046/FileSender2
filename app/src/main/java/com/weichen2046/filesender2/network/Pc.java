package com.weichen2046.filesender2.network;

import java.net.InetAddress;

/**
 * Created by chenwei on 12/10/16.
 */

public class Pc {
    public InetAddress addr;
    public int listenPort;
    public String name;

    public Pc(InetAddress addr, int listenPort) {
        this(addr, listenPort, null);
    }

    public Pc(InetAddress addr, int listenPort, String name) {
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
        if (!(obj instanceof Pc)) {
            return false;
        }

        return this.addr.equals(((Pc) obj).addr);
    }
}
