package com.weichen2046.filesender2.network;

/**
 * Created by chenwei on 12/4/16.
 */

public interface INetworkDefs {

    int PC_LISTEN_PORT = 4555;
    int BROAD_MONITOR_LISTEN_PORT = 4556;

    int DATA_VERSION = 1;

    // The first 4 bytes -> data version
    // The second 4 bytes -> cmd
    int MIN_DATA_LENGTH = 8;

    /////////////////// Phone -> PC ///////////////////
    int CMD_PHONE_ONLINE    = 1;
    int CMD_PHONE_OFFLINE   = 2;
    int CMD_SEND_FILE       = 3;

    /////////////////// PC -> Phone ///////////////////
    int CMD_PC_ONLINE       = 1;
    int CMD_PC_OFFLINE      = 2;
}
