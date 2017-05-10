package com.weichen2046.filesender2.network.tcp;

import android.util.Log;

import com.weichen2046.filesender2.network.tcp.state.DynamicLongLengthGetter;
import com.weichen2046.filesender2.network.tcp.state.DynamicStringGetter;
import com.weichen2046.filesender2.network.tcp.state.LongStateConsumer;
import com.weichen2046.filesender2.network.tcp.state.StateConsumer;
import com.weichen2046.filesender2.network.tcp.state.DynamicIntLengthGetter;
import com.weichen2046.filesender2.network.tcp.state.FileStateConsumer;
import com.weichen2046.filesender2.network.tcp.state.GroupStateConsumer;
import com.weichen2046.filesender2.network.tcp.state.IntStateConsumer;
import com.weichen2046.filesender2.network.tcp.state.NthStateConsumer;
import com.weichen2046.filesender2.network.tcp.state.StringStateConsumer;

/**
 * Created by chenwei on 2017/4/27.
 */

public class CmdSendFileHandler extends AuthTcpDataHandler {
    private int mNFileToReceive;
    private int[] mNFileNameLengths;
    private String[] mNFileNames;
    private long[] mNFileContentLengths;

    private int mCurrentFileIndex = 0;

    @Override
    public void onInitStates() {
        super.onInitStates();
        // file list length
        // Nth file name length
        // Nth file name
        // Nth file content length
        // Nth file content
        addStateConsumer(new IntStateConsumer(new StateConsumer.StateConsumerCallback() {
            @Override
            public boolean onDataParsed(Object value, byte[] remains) {
                mNFileToReceive = (int) value;
                mNFileNameLengths = new int[mNFileToReceive];
                mNFileNames = new String[mNFileToReceive];
                mNFileContentLengths = new long[mNFileToReceive];
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
        gsc.addConsumer(new LongStateConsumer(new StateConsumer.StateConsumerCallback() {
            @Override
            public boolean onDataParsed(Object value, byte[] remains) {
                long fileContentLength = (long) value;
                mNFileContentLengths[mCurrentFileIndex] = fileContentLength;
                Log.d(TAG, "recv file[" + mCurrentFileIndex + "], file content length: " + fileContentLength);
                return true;
            }
        }));
        gsc.addConsumer(
                new FileStateConsumer(new DynamicLongLengthGetter(this, "getCurrentFileContentLength"),
                        new DynamicStringGetter(this, "getCurrentFileName")));

        addStateConsumer(new NthStateConsumer(new DynamicIntLengthGetter(this, "getNthFileToRecv"),
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

    public String getCurrentFileName() {
        return mNFileNames[mCurrentFileIndex];
    }

    public long getCurrentFileContentLength() {
        return mNFileContentLengths[mCurrentFileIndex];
    }

    public int getNthFileToRecv() {
        return mNFileToReceive;
    }
}
