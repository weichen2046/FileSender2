package com.weichen2046.filesender2.network.tcp.state;

import android.util.Log;

import com.weichen2046.filesender2.MyApplication;
import com.weichen2046.filesender2.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by chenwei on 2017/4/29.
 */

public class FileStateConsumer extends StateConsumer {
    private String mFileName;
    private long mFileLength = IIntLengthGetter.UNKNOWN_LENGTH;
    private long mReceivedLength = 0;

    private ILongLengthGetter mFileLengthGetter;
    private IStringGetter mFileNameGetter;
    private BufferedOutputStream mFileOutputStream;

    public FileStateConsumer(ILongLengthGetter fileLengthGetter, IStringGetter fileNameGetter) {
        super(new UnknownIntLengthGetter(), null);
        mFileLengthGetter = fileLengthGetter;
        mFileNameGetter = fileNameGetter;
    }

    @Override
    protected Object onHandle(byte[] buffer) {
        long left = mFileLength - mReceivedLength;
        if (buffer.length > left) {
            // write data to file buffer[0, left]
            try {
                mFileOutputStream.write(buffer, 0, (int)left);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mReceivedLength += left;
            // calc remains bytes
            int remainsLength = buffer.length - (int)left;
            mRemains = new byte[remainsLength];
            System.arraycopy(buffer, (int)left, mRemains, 0, remainsLength);
        } else {
            // write data to file buffer[0, buffer.length]
            try {
                mFileOutputStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }

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
    protected void onReset() {
        super.onReset();
        mFileName = null;
        mFileLength = IIntLengthGetter.UNKNOWN_LENGTH;
        mReceivedLength = 0;
    }

    @Override
    protected void onBegin() {
        super.onBegin();
        // init file content length
        mFileLength = mFileLengthGetter.getLength();
        mFileName = mFileNameGetter.getString();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        String subdir = dateFormat.format(calendar.getTime());
        File destDir = new File(MyApplication.getInstance().getExternalFilesDir(null), subdir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File destFile = new File(destDir, mFileName);
        // open a writable file to recv data
        //Log.d(TAG, "dest dir: " + destDir.getAbsolutePath());
        Log.d(TAG, "dest file: " + destFile.getAbsolutePath());
        try {
            OutputStream os = new FileOutputStream(destFile);
            mFileOutputStream = new BufferedOutputStream(os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onEnd() {
        super.onEnd();
        // close writable file
        if (mFileOutputStream != null) {
            Utils.silenceClose(mFileOutputStream);
            mFileOutputStream = null;
        }
        // check recved length equals file content length
        if (mReceivedLength != mFileLength) {
            Log.w(TAG, "received file content length: " + mReceivedLength + ", file content length: " + mFileLength);
        }
    }
}
