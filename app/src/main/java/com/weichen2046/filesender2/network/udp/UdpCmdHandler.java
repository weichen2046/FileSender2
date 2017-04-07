package com.weichen2046.filesender2.network.udp;

import android.util.Log;

import com.weichen2046.filesender2.network.INetworkDefs;
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

    public static UdpCmdHandler getHandler(int cmd) {
        UdpCmdHandler handler = null;
        switch (cmd) {
            case INetworkDefs.CMD_R_DESKTOP_ONLINE:
            case INetworkDefs.CMD_R_DESKTOP_OFFLINE:
                handler = new DesktopOnlineOfflineCmdHandler(cmd);
                break;
            case INetworkDefs.CMD_R_CONFIRM_RECV_FILE:
                handler = new ConfirmRecvCmdHandler(cmd);
                break;
            case INetworkDefs.CMD_R_DESKTOP_REQUEST_AUTH:
                handler = new RequestAuthHandler(cmd);
                break;
            case INetworkDefs.CMD_R_EXCHANGE_TCP_PORT:
                handler = new ExchangeTcpPortHandler(cmd);
                break;
            case INetworkDefs.CMD_R_SENDING_FILE_REQ:
                handler = new SendingFileRequestHandler(cmd);
                break;
            default:
                Log.w(TAG, "unknown udp cmd: " + cmd);
        }
        return handler;
    }

    public abstract boolean handle(BroadcastData data);
}
