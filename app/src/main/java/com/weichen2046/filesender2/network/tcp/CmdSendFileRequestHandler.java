package com.weichen2046.filesender2.network.tcp;

import android.util.Log;

import com.weichen2046.filesender2.MyApplication;
import com.weichen2046.filesender2.network.tcp.state.DynamicIntLengthGetter;
import com.weichen2046.filesender2.network.tcp.state.GroupStateConsumer;
import com.weichen2046.filesender2.network.tcp.state.IntStateConsumer;
import com.weichen2046.filesender2.network.tcp.state.NthStateConsumer;
import com.weichen2046.filesender2.network.tcp.state.StateConsumer;
import com.weichen2046.filesender2.network.tcp.state.StringStateConsumer;
import com.weichen2046.filesender2.utils.NotificationHelper;

import java.util.Arrays;

/**
 * Created by chenwei on 2017/5/7.
 */

public class CmdSendFileRequestHandler extends AuthTcpDataHandler {
    private int mNFileToReceive;
    private int[] mNFileIDLengths;
    private String[] mNFileIDs;
    private int[] mNFileNameLengths;
    private String[] mNFileNames;
    private int mCurrentFileIndex = 0;

    @Override
    public void onInitStates() {
        super.onInitStates();
        // file list length
        // Nth file name length
        // Nth file name
        addStateConsumer(new IntStateConsumer(new StateConsumer.StateConsumerCallback() {
            @Override
            public boolean onDataParsed(Object value, byte[] remains) {
                mNFileToReceive = (int) value;
                mNFileIDLengths = new int[mNFileToReceive];
                mNFileIDs = new String[mNFileToReceive];
                mNFileNameLengths = new int[mNFileToReceive];
                mNFileNames = new String[mNFileToReceive];
                return true;
            }
        }));

        GroupStateConsumer gsc = new GroupStateConsumer();
        gsc.addConsumer(new IntStateConsumer(new StateConsumer.StateConsumerCallback() {
            @Override
            public boolean onDataParsed(Object value, byte[] remains) {
                int fileNameLength = (int) value;
                mNFileNameLengths[mCurrentFileIndex] = fileNameLength;
                Log.d(TAG, "recv file[" + mCurrentFileIndex + "], name length: " + fileNameLength);
                return true;
            }
        }));
        gsc.addConsumer(new StringStateConsumer(
                new DynamicIntLengthGetter(this, "getCurrentFileNameLength"),
                new StateConsumer.StateConsumerCallback() {
                    @Override
                    public boolean onDataParsed(Object value, byte[] remains) {
                        String fileName = value.toString();
                        mNFileNames[mCurrentFileIndex] = fileName;
                        Log.d(TAG, "recv file[" + mCurrentFileIndex + "], name: " + fileName);
                        return true;
                    }
                }));
        gsc.addConsumer(new IntStateConsumer(new StateConsumer.StateConsumerCallback() {
            @Override
            public boolean onDataParsed(Object value, byte[] remains) {
                int fileIdLength = (int) value;
                mNFileIDLengths[mCurrentFileIndex] = fileIdLength;
                Log.d(TAG, "recv file[" + mCurrentFileIndex + "], id length: " + fileIdLength);
                return true;
            }
        }));
        gsc.addConsumer(new StringStateConsumer(
                new DynamicIntLengthGetter(this, "getCurrentFileIDLength"),
                new StateConsumer.StateConsumerCallback() {
                    @Override
                    public boolean onDataParsed(Object value, byte[] remains) {
                        String fileId = value.toString();
                        mNFileIDs[mCurrentFileIndex] = fileId;
                        Log.d(TAG, "recv file[" + mCurrentFileIndex + "], id: " + fileId);
                        return true;
                    }
                }));

        addStateConsumer(new NthStateConsumer(new DynamicIntLengthGetter(this, "getFileListLength"),
                gsc, new NthStateConsumer.INthChangeListener() {
            @Override
            public void onNthChanged(int n) {
                mCurrentFileIndex = n;
            }
        }));
    }

    public int getCurrentFileNameLength() {
        return mNFileNameLengths[mCurrentFileIndex];
    }

    public int getCurrentFileIDLength() {
        return mNFileIDLengths[mCurrentFileIndex];
    }

    public int getFileListLength() {
        return mNFileToReceive;
    }

    @Override
    protected void onEnd(boolean isOK) {
        super.onEnd(isOK);
        Log.d(TAG, "cmd send file request handle complete, files: " + Arrays.toString(mNFileNames));
        NotificationHelper.notifySendFileRequest(MyApplication.getInstance(), mAuthedDevice,
                mNFileIDs, mNFileNames);
    }
}
