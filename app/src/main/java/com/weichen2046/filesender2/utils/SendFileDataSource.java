package com.weichen2046.filesender2.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.utils.byteconvertor.BytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.IntBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.LongBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.StringBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.TransparentBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.UriFileBytesConvertor;

import java.util.UUID;

/**
 * Created by chenwei on 2017/2/9.
 */

public class SendFileDataSource extends ByteDataSource {
    private Context mContext;
    private Uri mFileUri;
    private Desktop mDesktop;

    public SendFileDataSource(Context context, Uri fileUri, Desktop desktop) {
        mContext = context;
        mFileUri = fileUri;
        mDesktop = desktop;
    }

    @Override
    public boolean onInit() {
        super.onInit();

        String uuid = UUID.randomUUID().toString().replace("-", "");
        // use uuid as default file name
        String fileName = uuid;
        long fileSize = 0;

        Cursor cursor = null;
        try {
            ContentResolver cr = mContext.getContentResolver();
            // get file name
            cursor = cr.query(mFileUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                fileSize = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
            }
        } finally {
            Utils.silenceClose(cursor);
        }

        // data version
        fillData(new IntBytesConvertor(INetworkDefs.DATA_VERSION));
        // network cmd
        fillData(new IntBytesConvertor(INetworkDefs.CMD_SEND_FILE));
        // write token length
        fillData(new IntBytesConvertor(mDesktop.accessToken.length()));
        // write token
        fillData(new StringBytesConvertor(mDesktop.accessToken));

        BytesConvertor convertor = new StringBytesConvertor(fileName);
        byte[] fileNameBytes = convertor.getBytes();
        // file name length
        fillData(new IntBytesConvertor(fileNameBytes.length));
        // file name
        fillData(new TransparentBytesConvertor(fileNameBytes));
        // file content length
        fillData(new LongBytesConvertor(fileSize));
        // file content
        fillData(new UriFileBytesConvertor(mContext, mFileUri));

        return true;
    }
}
