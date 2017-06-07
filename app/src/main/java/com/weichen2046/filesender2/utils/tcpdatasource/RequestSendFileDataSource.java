package com.weichen2046.filesender2.utils.tcpdatasource;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.weichen2046.filesender2.db.FileSendingDataSource;
import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.utils.ByteDataSource;
import com.weichen2046.filesender2.utils.Utils;
import com.weichen2046.filesender2.utils.byteconvertor.BooleanBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.BytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.IntBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.LongBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.StringBytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.TransparentBytesConvertor;

import java.util.UUID;

/**
 * Created by chenwei on 2017/2/7.
 */

public class RequestSendFileDataSource extends ByteDataSource {
    private Context mContext;
    private Uri mFileUri;
    private Desktop mDesktop;

    public RequestSendFileDataSource(Context context, Uri fileUri, Desktop desktop) {
        mContext = context;
        mFileUri = fileUri;
        mDesktop = desktop;
    }

    @Override
    public boolean onInit() {
        super.onInit();

        String uuid = UUID.randomUUID().toString().replace("-", "");
        FileSendingDataSource dataSource = new FileSendingDataSource(mContext);
        dataSource.open();
        long fileId = dataSource.add(uuid, mFileUri, mDesktop.getAddress(), mDesktop.getTcpPort());
        dataSource.close();

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
        Log.d(TAG, "file to be sent: " + fileName + ", file size: " + fileSize + ", id:" + fileId + ", uuid: " + uuid);

        boolean hasThumnail = false;

        // write data version
        fillData(new IntBytesConvertor(INetworkDefs.DATA_VERSION));
        // write network cmd
        fillData(new IntBytesConvertor(INetworkDefs.CMD_T_SENDING_FILE_REQ));
        // write token length
        fillData(new IntBytesConvertor(mDesktop.getAccessToken().length()));
        // write token
        fillData(new StringBytesConvertor(mDesktop.getAccessToken()));

        BytesConvertor convertor = new StringBytesConvertor(fileName);
        byte[] fileNameBytes = convertor.getBytes();
        // file name length
        fillData(new IntBytesConvertor(fileNameBytes.length));
        // file name
        fillData(new TransparentBytesConvertor(fileNameBytes));
        // file id
        fillData(new LongBytesConvertor(fileId));
        // thumbnail indicator
        fillData(new BooleanBytesConvertor(hasThumnail));

        if (hasThumnail) {
            // TODO: fill thumbnail length and data
        }
        return true;
    }
}
