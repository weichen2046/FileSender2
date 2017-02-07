package com.weichen2046.filesender2.network.udp;

import com.weichen2046.filesender2.network.INetworkDefs;

/**
 * Created by chenwei on 2017/1/31.
 */

public abstract class UdpCmdHandler {

    protected  static final String TAG = "UdpCmdHandler";

    protected int mCmd;

    public UdpCmdHandler(int cmd) {
        mCmd = cmd;
    }

    public static UdpCmdHandler getHandler(int cmd) {
        switch (cmd) {
            case INetworkDefs.CMD_PC_ONLINE:
            case INetworkDefs.CMD_PC_OFFLINE:
                return new DesktopOnlineOfflineCmdHandler(cmd);
            case INetworkDefs.CMD_CONFIRM_RECV_FILE:
                return new ConfirmRecvCmdHandler(cmd);
        }
        return null;
    }

    public abstract void handle(BroadcastData data);
}
