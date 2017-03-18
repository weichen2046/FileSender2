package com.weichen2046.filesender2.network.udp;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManagerHolder;

/**
 * Created by chenwei on 2017/1/31.
 */

public abstract class UdpCmdHandler extends ServiceManagerHolder {

    protected  static final String TAG = "UdpCmdHandler";

    protected int mCmd;

    public UdpCmdHandler(int cmd) {
        mCmd = cmd;
    }

    public static UdpCmdHandler getHandler(int cmd, IServiceManager manager) {
        UdpCmdHandler handler = null;
        switch (cmd) {
            case INetworkDefs.CMD_PC_ONLINE:
            case INetworkDefs.CMD_PC_OFFLINE:
                handler = new DesktopOnlineOfflineCmdHandler(cmd);
            case INetworkDefs.CMD_CONFIRM_RECV_FILE:
                handler = new ConfirmRecvCmdHandler(cmd);
            case INetworkDefs.CMD_PC_REQUEST_AUTH:
                handler = new RequestAuthHandler(cmd);
        }
        handler.attach(manager);
        return handler;
    }

    public abstract void handle(BroadcastData data);
}
