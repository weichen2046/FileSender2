package com.weichen2046.filesender2.network;

import java.net.InetAddress;

/**
 * Created by chenwei on 12/10/16.
 */

public class DesktopMachine {
    public InetAddress addr;
    public int listenPort;
    public String name;

    public DesktopMachine(InetAddress addr, int listenPort) {
        this(addr, listenPort, null);
    }

    public DesktopMachine(InetAddress addr, int listenPort, String name) {
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
        if (!(obj instanceof DesktopMachine)) {
            return false;
        }

        return this.addr.equals(((DesktopMachine) obj).addr);
    }
}
