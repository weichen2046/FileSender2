package com.weichen2046.filesender2.network;

import java.net.InetAddress;

/**
 * Created by chenwei on 12/10/16.
 */

public class BroadcastData {
    public InetAddress addr;
    public int port;
    public byte[] data;

    public BroadcastData(InetAddress addr, int port, byte[] data) {
        this.addr = addr;
        this.port = port;
        this.data = data;
    }
}
