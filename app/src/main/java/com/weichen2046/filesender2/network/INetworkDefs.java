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
    int CMD_REPORT_PHONE_BROAD_MONITOR_PORT = 1;
    int CMD_MAKE_PHONE_BROAD_MONITOR_EXIT = 2;

    /////////////////// PC -> Phone ///////////////////
    int CMD_REPORT_PC_MONITOR_PORT = 1;
}
