package com.weichen2046.filesender2.utils.byteconvertor;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.weichen2046.filesender2.utils.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by chenwei on 2017/2/7.
 */

public class UriFileBytesConvertor extends BytesConvertor {
    private static final String TAG = "UriFileBytesConvertor";

    private Context mContext;
    private Uri mFileUri;
    private ParcelFileDescriptor mFileDescriptor;
    private FileInputStream mFileInputStream;
    private static final int BUFFER_SIZE = 2046;
    private byte[] mBuff;
    private int mTmpRead;

    public UriFileBytesConvertor(Context context, Uri fileUri) {
        mContext = context;
        mFileUri = fileUri;
    }

    @Override
    public byte[] onGetBytes() {
        try {
            mTmpRead = mFileInputStream.read(mBuff);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mTmpRead > 0) {
            if (mTmpRead == BUFFER_SIZE) {
                return mBuff;
            } else {
                return Arrays.copyOf(mBuff, mTmpRead);
            }
        } else {
            markEnded();
            return null;
        }
    }

    @Override
    public void init() {
        super.init();
        ContentResolver cr = mContext.getContentResolver();
        try {
            mFileDescriptor = cr.openFileDescriptor(mFileUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mFileInputStream = new FileInputStream(mFileDescriptor.getFileDescriptor());
        mBuff = new byte[BUFFER_SIZE];
    }

    @Override
    public void destroy() {
        super.destroy();
        mBuff = null;
        Utils.silenceClose(mFileInputStream);
        Utils.silenceClose(mFileDescriptor);
    }
}
