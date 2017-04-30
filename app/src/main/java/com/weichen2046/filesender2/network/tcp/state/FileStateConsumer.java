package com.weichen2046.filesender2.network.tcp.state;

import android.util.Log;

/**
 * Created by chenwei on 2017/4/29.
 */

public class FileStateConsumer extends StateConsumer {
    private String mFileName;
    private long mFileLength = IIntLengthGetter.UNKNOWN_LENGTH;
    private long mReceivedLength = 0;

    private ILongLengthGetter mFileLengthGetter;
    private IStringGetter mFileNameGetter;

    public FileStateConsumer(ILongLengthGetter fileLengthGetter, IStringGetter fileNameGetter) {
        super(new UnknownIntLengthGetter(), null);
        mFileLengthGetter = fileLengthGetter;
        mFileNameGetter = fileNameGetter;
    }

    @Override
    protected Object onHandle(byte[] buffer) {
        long left = mFileLength - mReceivedLength;
        if (buffer.length > left) {
            // TODO: write data to file buffer[0, left]

            mReceivedLength += left;
            // calc remains bytes
            int remainsLength = buffer.length - (int)left;
            mRemains = new byte[remainsLength];
            System.arraycopy(buffer, (int)left, mRemains, 0, remainsLength);
        } else {
            // TODO: write data to file buffer[0, buffer.length]

            mReceivedLength += buffer.length;
            mRemains = null;
        }
        return null;
    }

    @Override
    protected HandleState getHandleState() {
        if (mReceivedLength < mFileLength) {
            return HandleState.MORE_DATA;
        }
        return super.getHandleState();
    }

    @Override
    protected void onBegin() {
        super.onBegin();
        // init file content length
        mFileLength = mFileLengthGetter.getLength();
        mFileName = mFileNameGetter.getString();
        // TODO: open a writable file to recv data
    }

    @Override
    protected void onEnd() {
        super.onEnd();
        // TODO: close writable file
        // check recved length equals file content length
        if (mReceivedLength != mFileLength) {
            Log.w(TAG, "received file content length: " + mReceivedLength + ", file content length: " + mFileLength);
        }
    }
}
