package com.weichen2046.filesender2.network;

/**
 * Created by chenwei on 12/4/16.
 */

public interface INetworkDefs {

    int DEFAULT_DESKTOP_TCP_PORT    = 6852;

    int DESKTOP_UDP_LISTEN_PORT     = 4555;
    int MOBILE_UDP_LISTEN_PORT      = 4556;

    int DATA_VERSION = 1;

    // The first 4 bytes -> data version
    // The second 4 bytes -> cmd
    int MIN_DATA_LENGTH = 8;

    byte HAS_NOT_THUMBNAIL  = 0;
    byte HAS_THUMBNAIL      = 1;

    /////////////////// Phone -> Desktop ///////////////////
    int CMD_T_PHONE_ONLINE              = 1;
    int CMD_T_PHONE_OFFLINE             = 2;
    int CMD_T_SEND_FILE                 = 3;
    int CMD_T_SENDING_FILE_REQ          = 4;
    int CMD_T_CONFIRM_DESKTOP_AUTH_REQ  = 5;
    int CMD_T_CONFIRM_EXCHANGE_TCP_PORT = 6;

    /////////////////// Desktop -> Phone ///////////////////
    int CMD_R_DESKTOP_ONLINE            = 1;
    int CMD_R_DESKTOP_OFFLINE           = 2;
    int CMD_R_CONFIRM_RECV_FILE         = 3;
    int CMD_R_DESKTOP_REQUEST_AUTH      = 4;
    int CMD_R_SENDING_FILE_REQ          = 5;
    int CMD_R_EXCHANGE_TCP_PORT         = 6;
}
