package com.weichen2046.filesender2.network.udp;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by chenwei on 2017/4/7.
 */

public class SendingFileRequestHandler extends UdpAuthCmdHandler {
    public SendingFileRequestHandler(int cmd) {
        super(cmd);
    }

    @Override
    public boolean handle(BroadcastData data) {
        boolean res = super.handle(data);
        if (!res) {
            return false;
        }
        // read paths length
        int pathsLenght = mBuffer.getInt();
        //Log.d(TAG, "SendingFileRequestHandler, auth desktop: " + mDesktop + ", paths length: " + pathsLenght);

        ArrayList<String> paths = new ArrayList<>();
        while(pathsLenght > 0 && mBuffer.hasRemaining()) {
            // read path length
            int len = mBuffer.getInt();
            byte[] pathBytes = new byte[len];
            // read path
            mBuffer.get(pathBytes);
            String path = new String(pathBytes);
            //Log.d(TAG, "SendingFileRequestHandler, get path: " + path);
            paths.add(path);
            pathsLenght--;
        }

        if (paths.size() > 0) {
            // TODO: notify user of sending file reqeust from desktop
        }

        return true;
    }
}
