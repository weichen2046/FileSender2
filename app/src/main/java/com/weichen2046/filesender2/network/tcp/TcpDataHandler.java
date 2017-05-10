package com.weichen2046.filesender2.network.tcp;

import android.util.Log;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.network.tcp.state.CmdStateConsumer;
import com.weichen2046.filesender2.network.tcp.state.IntStateConsumer;
import com.weichen2046.filesender2.network.tcp.state.StateConsumer;
import com.weichen2046.filesender2.service.ServiceManagerHolder;

import java.util.ArrayList;


/**
 * Created by chenwei on 2017/4/9.
 */

public class TcpDataHandler extends ServiceManagerHolder {
    public static final String TAG = "TcpDataHandler";

    private TcpDataHandler mInnerConsumer = null;

    private ArrayList<StateConsumer> mConsumers = new ArrayList<>();
    private int mConsumerIndex = 0;

    protected String mRemoteHost;
    protected int mRemotePort;
    protected int mVersion;
    protected int mCmd;
    protected byte[] mRemains;

    public StateConsumer.HandleState handle(byte[] buffer) {
        if (mInnerConsumer != null) {
            return mInnerConsumer.handle(buffer);
        } else {
            byte[] data = mergeData(buffer);
            if (mConsumerIndex >= mConsumers.size()) {
                Log.d(TAG, "current index out of bounds");
                return StateConsumer.HandleState.FAIL;
            }
            StateConsumer consumer = mConsumers.get(mConsumerIndex);
            consumer.initializedIfNeeded();
            StateConsumer.HandleState res = consumer.handle(data);
            mRemains = consumer.getAndResetRemains();
            if (res == StateConsumer.HandleState.OK) {
                consumer.destroyIfNeeded();
                mConsumerIndex++;
                if (mConsumerIndex == mConsumers.size()) {
                    end(true);
                }
            }
            return res;
        }
    }

    public void init(int version, int cmd, byte[] data, String host, int port) {
        mRemoteHost = host;
        mRemotePort = port;
        mVersion = version;
        mCmd = cmd;
        mRemains = data;
        onInitStates();
    }

    public void end(boolean isOK) {
        onEnd(isOK);
    }

    protected void addStateConsumer(StateConsumer consumer) {
        mConsumers.add(consumer);
    }

    protected void onInitStates() {
        addStateConsumer(new IntStateConsumer(new StateConsumer.StateConsumerCallback() {
            @Override
            public boolean onDataParsed(Object value, byte[] remains) {
                mVersion = (int) value;
                Log.d(TAG, "version: " + mVersion);
                return true;
            }
        }));
        addStateConsumer(new CmdStateConsumer(new StateConsumer.StateConsumerCallback() {
            @Override
            public boolean onDataParsed(Object value, byte[] remains) {
                mCmd = (int) value;
                Log.d(TAG, "cmd: " + mCmd);
                mInnerConsumer = getDataConsumerFromCmd(mCmd, mVersion, remains);
                return true;
            }
        }));
    }

    protected void onEnd(boolean isOK) {
    }

    private byte[] mergeData(byte[] data) {
        if (mRemains == null) {
            return data;
        }
        if (data == null) {
            return mRemains;
        }
        byte[] buff = new byte[mRemains.length + data.length];
        System.arraycopy(mRemains, 0, buff, 0, mRemains.length);
        System.arraycopy(data, 0, buff, mRemains.length, data.length);
        return buff;
    }

    private TcpDataHandler getDataConsumerFromCmd(int cmd, int version, byte[] data) {
        TcpDataHandler consumer = null;
        switch (cmd) {
            case INetworkDefs.CMD_R_SENDING_FILE_REQ:
                consumer = new CmdSendFileRequestHandler();
                break;
            case INetworkDefs.CMD_R_SEND_FILE:
                consumer = new CmdSendFileHandler();
                break;
            default:
                Log.w(TAG, "unknown cmd: " + cmd);
                break;
        }
        if (consumer != null) {
            consumer.init(version, cmd, data, mRemoteHost, mRemotePort);
            consumer.attach(getServiceManager());
        }
        return consumer;
    }
}
